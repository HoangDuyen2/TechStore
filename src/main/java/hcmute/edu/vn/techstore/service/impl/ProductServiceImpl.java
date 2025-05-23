package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.builder.ProductFilterBuilder;
import hcmute.edu.vn.techstore.convert.ProductFilterBuilderConverter;
import hcmute.edu.vn.techstore.convert.ProductMapper;
import hcmute.edu.vn.techstore.convert.ProductResponseConvert;
import hcmute.edu.vn.techstore.dto.ProductDTO;
import hcmute.edu.vn.techstore.dto.response.*;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.entity.OrderDetailEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.repository.BrandRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.repository.custome.ProductRepositoryCustom;
import hcmute.edu.vn.techstore.service.interfaces.IProductService;
import hcmute.edu.vn.techstore.utils.ImageUtil;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productConverter;
    private final ProductFilterBuilderConverter productFilterBuilderConverter;
    private final ImageUtil imageUtil;
    private final ProductResponseConvert productResponseConvert;
    private final PriceUtil priceUtil;


    @Override
    public void saveProduct(ProductDTO product, MultipartFile file, String existingImagePath) {
        String img = (product.getThumbnail() != null && !product.getThumbnail().isEmpty())
                ? product.getThumbnail()
                : "https://res.cloudinary.com/techstore2025/image/upload/v1745587682/default-phone_p5dcoi.jpg";

        try {
            // Kiểm tra nếu có ảnh mới được tải lên
            if (file != null && !file.isEmpty()) {
                // Nếu có ảnh cũ và không phải ảnh mặc định, xóa ảnh cũ
                if (existingImagePath != null && !existingImagePath.isEmpty() && !existingImagePath.equals("https://res.cloudinary.com/techstore2025/image/upload/v1745587682/default-phone_p5dcoi.jpg")) {
                    imageUtil.deleteImage(existingImagePath);
                }

                // Lưu ảnh mới
                if (imageUtil.isValidSuffixImage(Objects.requireNonNull(file.getOriginalFilename()))) {
                    img = imageUtil.saveImage(file);
                } else {
                    throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, PNG, GIF, BMP are allowed.");
                }
            }

            // Gán lại giá trị đường dẫn ảnh (mới hoặc cũ)
            product.setThumbnail(img);

            Optional<BrandEntity> brand = brandRepository.findById(product.getBrandId());

            if (brand.isPresent()) {
                ProductEntity productEntity = productConverter.toEntity(product);
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
                    ProductDTO productDTO = productConverter.toDTO(productEntity);
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
                    ProductDTO productDTO = productConverter.toDTO(productEntity);
                    productDTO.setBrandId(productEntity.getBrand().getId());
                    return productDTO;
                })
                .toList();
    }

    @Override
    public Page<ProductCollectionResponse> filterProducts(Map<String, Object> params, Pageable pageable) {
        ProductFilterBuilder builder = ProductFilterBuilderConverter.toProductFilterBuilder(params);

        Page<ProductEntity> productPage = productRepository.findAll(
                ProductRepositoryCustom.filter(builder), pageable);

        return productPage.map(product -> ProductCollectionResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .thumbnail(product.getThumbnail())
                .price(priceUtil.formatPrice(product.getPrice()))
                .stars(product.getStar() != null ? product.getStar() : 0)
                .build());
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
                        .price(priceUtil.formatPrice(product.getPrice()))
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
                        .price(priceUtil.formatPrice(product.getPrice()))
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
//        List<ProductEntity> mostBuyingProducts = productRepository.findByOrderByCreatedDateDesc();
        // Get 20 most buying products
        List<ProductEntity> mostBuyingProducts = productRepository.findTopMostBuyingProductsByOrderDetail();
        for (ProductEntity product : mostBuyingProducts) {
            if (product.isActived() && product.getBrand().getIsActived()) {
                productHomeSliders.add(ProductHomeTrending.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .thumbnail(product.getThumbnail())
                        .price(priceUtil.formatPrice(product.getPrice()))
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

    @Override
    public Optional<ProductResponse> findProductResponseById(Long id) {
        ProductEntity p = productRepository.findById(id).orElse(null);
        if (p != null) {
            return Optional.ofNullable(productResponseConvert.toProductResponse(p));
        }
        return null;
    }

    @Override
    public List<ProductSearchSuggestion> searchSuggestions(String keyword) {
        List<ProductEntity> products = productRepository.searchByKeywordAndActivedBrand(keyword);
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return products.stream().map(p -> new ProductSearchSuggestion(
                p.getId(),
                p.getName(),
                p.getThumbnail(),
                formatter.format(p.getPrice())
        )).collect(Collectors.toList());
    }

    @Override
    public boolean decreaseQuantity(Long id, int quantity) {
        ProductEntity product = productRepository.findById(id).get();
        // Decrease product quantity
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
        return true;
    }

    @Override
    public Optional<ProductEntity> findById(Long aLong) {
        return productRepository.findById(aLong);
    }

    @Override
    public String getTotalAvailableProducts() {
        List<ProductEntity> products = productRepository.findAll();
        int totalAvailableProducts = 0;
        for (ProductEntity product : products) {
            if (product.isActived() && product.getBrand().getIsActived()) {
                totalAvailableProducts++;
            }
        }
        return String.valueOf(totalAvailableProducts);
    }

    @Override
    public List<AdminDashboardTopSellingProduct> getTopSellingProducts() {
        List<ProductEntity> products = productRepository.findAll();
        List<AdminDashboardTopSellingProduct> topSellingProducts = new ArrayList<>();
        for (ProductEntity product : products) {
            if (product.isActived() && product.getBrand().getIsActived()) {
                AdminDashboardTopSellingProduct topSellingProduct = new AdminDashboardTopSellingProduct();
                topSellingProduct.setProductId(product.getId());
                topSellingProduct.setProductName(product.getName());
                topSellingProduct.setProductImage(product.getThumbnail());
                int totalSold = product.getOrderDetails().stream()
                        .mapToInt(OrderDetailEntity::getQuantity)
                        .sum();
                topSellingProduct.setTotalSold(String.valueOf(totalSold));
                topSellingProduct.setStock(String.valueOf(product.getStockQuantity()));
                topSellingProduct.setBrand(product.getBrand().getName());
                topSellingProducts.add(topSellingProduct);
            }
        }
        // Sort by total sold descending
        topSellingProducts.sort((p1, p2) -> Integer.compare(
                Integer.parseInt(p2.getTotalSold()),
                Integer.parseInt(p1.getTotalSold())
        ));
        return topSellingProducts;
    }
}
