package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.builder.ProductFilterBuilder;
import hcmute.edu.vn.techstore.convert.ProductFilterBuilderConverter;
import hcmute.edu.vn.techstore.convert.ProductMapper;
import hcmute.edu.vn.techstore.dto.ProductDTO;
import hcmute.edu.vn.techstore.dto.response.ProductHomeSlider;
import hcmute.edu.vn.techstore.dto.response.ProductHomeTrending;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.entity.ReviewEntity;
import hcmute.edu.vn.techstore.repository.BrandRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.repository.custome.ProductRepositoryCustom;
import hcmute.edu.vn.techstore.service.interfaces.IProductService;
import hcmute.edu.vn.techstore.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;



@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productDTOConverter;
    private final ProductFilterBuilderConverter productFilterBuilderConverter;
    private final ImageUtil imageUtil;

    @Override
    public void saveProduct(ProductDTO product, MultipartFile file, String existingImagePath) {
        String img = (product.getThumbnail() != null && !product.getThumbnail().isEmpty())
                ? product.getThumbnail()
                : "default-product.jpg";

        try {
            // Kiểm tra nếu có ảnh mới được tải lên
            if (file != null && !file.isEmpty()) {
                // Nếu có ảnh cũ và không phải ảnh mặc định, xóa ảnh cũ
                if (existingImagePath != null && !existingImagePath.isEmpty() && !existingImagePath.equals("default-product.jpg")) {
                    imageUtil.deleteImage(existingImagePath); // ✅ Gọi qua bean
                }

                // Lưu ảnh mới
                if (imageUtil.isValidSuffixImage(Objects.requireNonNull(file.getOriginalFilename()))) {
                    img = imageUtil.saveImage(file); // ✅ Gọi qua bean
                } else {
                    throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, PNG, GIF, BMP are allowed.");
                }
            }

            // Gán lại giá trị đường dẫn ảnh (mới hoặc cũ)
            product.setThumbnail(img);

            // Ánh xạ và lưu vào database
            Optional<BrandEntity> brand = brandRepository.findById(product.getBrandId());

            if (brand.isPresent()) {
                ProductEntity productEntity = productDTOConverter.toEntity(product);
                productEntity.setBrand(brand.get());
                productRepository.save(productEntity);
            } else {
                throw new IllegalArgumentException("Brand not found with ID: " + product.getBrandId());
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save the image: " + e.getMessage(), e);
        }
    }


    @Override
    public ProductDTO findProductById(Long id) {
        return productRepository.findById(id)
                .map(productEntity -> {
                    ProductDTO productDTO = productDTOConverter.toDTO(productEntity);
                    productDTO.setBrandId(productEntity.getBrand().getId());
                    return productDTO;
                })
                .orElse(null);
    }

    @Override
    public List<ProductDTO> findAllProduct() {
        return productRepository.findAll()
                .stream()
                .map(productEntity -> {
                    ProductDTO productDTO = productDTOConverter.toDTO(productEntity);
                    productDTO.setBrandId(productEntity.getBrand().getId());
                    return productDTO;
                })
                .toList();
    }

    @Override
    public Page<ProductEntity> filterProducts(Map<String, Object> params, Pageable pageable) {
        ProductFilterBuilder builder = ProductFilterBuilderConverter.toProductFilterBuilder(params);

        Page<ProductEntity> productPage = productRepository.findAll(
                ProductRepositoryCustom.filter(builder), pageable);
        return productPage;
    }

    @Override
    public List<ProductHomeSlider> getProductHomeSlider() {
        List<ProductHomeSlider> productHomeSliders = new ArrayList<>();

        // get 2 latest products
        List<ProductEntity> newestProduct = productRepository.findByOrderByCreatedDateDesc();
        for (ProductEntity product : newestProduct) {
            if (product.isActived() && product.getBrand().getIsActived()) {
                productHomeSliders.add(ProductHomeSlider.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .thumbnail(product.getThumbnail())
                        .price(String.valueOf(product.getPrice()))
                        .build());
            }
            if (productHomeSliders.size() == 2) {
                break; // Chỉ lấy 2 sản phẩm mới nhất
            }
        }

        // Get 2 most buying products
        List<ProductEntity> mostBuyingProducts = productRepository.findTopMostBuyingProductsByOrderDetail();
        for (ProductEntity product : mostBuyingProducts) {
            if (product.isActived() && product.getBrand().getIsActived()) {
                productHomeSliders.add(ProductHomeSlider.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .thumbnail(product.getThumbnail())
                        .price(String.valueOf(product.getPrice()))
                        .build());
            }
            if (productHomeSliders.size() == 4) {
                break; // Chỉ lấy 2 sản phẩm bán chạy nhất
            }
        }

        return productHomeSliders;
    }

    @Override
    public List<ProductHomeTrending> getProductHomeTrending() {
        List<ProductHomeTrending> productHomeSliders = new ArrayList<>();

//         This line for testing
        List<ProductEntity> mostBuyingProducts = productRepository.findByOrderByCreatedDateDesc();
        // Get 20 most buying products
//        List<ProductEntity> mostBuyingProducts = productRepository.findTopMostBuyingProductsByOrderDetail();
        for (ProductEntity product : mostBuyingProducts) {
            if (product.isActived() && product.getBrand().getIsActived()) {
                productHomeSliders.add(ProductHomeTrending.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .thumbnail(product.getThumbnail())
                        .price(String.valueOf(product.getPrice()))
                        .stars(product.getStar() != null ? product.getStar() : 0)
                        .description(product.getDescription() != null ? product.getDescription() : "")
                        .build());
            }
            if (productHomeSliders.size() == 20) {
                break; // Chỉ lấy 20 sản phẩm bán chạy nhất
            }
        }

        return productHomeSliders;
    }
}
