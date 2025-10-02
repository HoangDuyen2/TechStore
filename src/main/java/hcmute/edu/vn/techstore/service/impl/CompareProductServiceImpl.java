package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.response.CompareProductResponse;
import hcmute.edu.vn.techstore.entity.CompareProductEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.CompareProductRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.ICompareProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompareProductServiceImpl implements ICompareProductService {
    
    private final CompareProductRepository compareProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public boolean addProductToCompare(Long userId, Long productId) {
        // Kiểm tra số lượng sản phẩm đã có trong compare list (tối đa 2)
        int currentCount = compareProductRepository.countByUser_IdAndIsActiveTrue(userId);
        if (currentCount >= 2) {
            throw new RuntimeException("Bạn chỉ có thể so sánh 2 sản phẩm với nhau.");
        }
        
        // Kiểm tra xem sản phẩm đã có trong compare list chưa
        Optional<CompareProductEntity> existingCompare = compareProductRepository
                .findByUser_IdAndProduct_IdAndIsActiveTrue(userId, productId);
        if (existingCompare.isPresent()) {
            return false; // Sản phẩm đã có trong compare list
        }
        
        // Kiểm tra user và product có tồn tại không
        Optional<UserEntity> user = userRepository.findById(userId);
        Optional<ProductEntity> product = productRepository.findById(productId);
        
        if (user.isEmpty() || product.isEmpty()) {
            throw new RuntimeException("User hoặc Product không tồn tại.");
        }
        
        // Thêm sản phẩm vào compare list
        CompareProductEntity compareProduct = CompareProductEntity.builder()
                .user(user.get())
                .product(product.get())
                .isActive(true)
                .build();
        
        compareProductRepository.save(compareProduct);
        return true;
    }
    
    @Override
    @Transactional
    public boolean removeProductFromCompare(Long userId, Long productId) {
        Optional<CompareProductEntity> compareProduct = compareProductRepository
                .findByUser_IdAndProduct_IdAndIsActiveTrue(userId, productId);
        
        if (compareProduct.isPresent()) {
            CompareProductEntity entity = compareProduct.get();
            entity.setIsActive(false);
            compareProductRepository.save(entity);
            return true;
        }
        return false;
    }
    
    @Override
    public List<CompareProductResponse> getCompareProducts(Long userId) {
        List<CompareProductEntity> compareProducts = compareProductRepository
                .findByUser_IdAndIsActiveTrue(userId);
        
        return compareProducts.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    @Override
    @Transactional
    public boolean clearCompareList(Long userId) {
        List<CompareProductEntity> compareProducts = compareProductRepository
                .findByUser_IdAndIsActiveTrue(userId);
        
        for (CompareProductEntity compareProduct : compareProducts) {
            compareProduct.setIsActive(false);
            compareProductRepository.save(compareProduct);
        }
        return true;
    }
    
    @Override
    public int getCompareProductCount(Long userId) {
        return compareProductRepository.countByUser_IdAndIsActiveTrue(userId);
    }
    
    @Override
    public List<CompareProductResponse> compareTwoProducts(Long productId1, Long productId2) {
        if (productId1.equals(productId2)) {
            throw new RuntimeException("Không thể so sánh sản phẩm với chính nó.");
        }
        
        List<ProductEntity> products = productRepository.findAllById(List.of(productId1, productId2));
        
        if (products.size() != 2) {
            throw new RuntimeException("Không tìm thấy đủ 2 sản phẩm để so sánh.");
        }
        
        return products.stream()
                .map(this::convertProductToResponse)
                .toList();
    }
    
    @Override
    public List<CompareProductResponse> searchProductsForCompare(String keyword, Long excludeProductId) {
        List<ProductEntity> products = productRepository.searchByKeywordAndActivedBrand(keyword);
        
        return products.stream()
                .filter(product -> !product.getId().equals(excludeProductId))
                .limit(10) // Giới hạn 10 kết quả
                .map(this::convertProductToResponse)
                .toList();
    }
    
    private CompareProductResponse convertToResponse(CompareProductEntity entity) {
        return convertProductToResponse(entity.getProduct());
    }
    
    private CompareProductResponse convertProductToResponse(ProductEntity product) {
        return CompareProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .warranty(product.getWarranty())
                .batteryCapacity(product.getBatteryCapacity())
                .frontCamera(product.getFrontCamera())
                .rearCamera(product.getRearCamera())
                .connectivity(product.getConnectivity())
                .stockQuantity(product.getStockQuantity())
                .operatingSystem(product.getOperatingSystem())
                .processor(product.getProcessor())
                .sim(product.getSim())
                .thumbnail(product.getThumbnail())
                .star(product.getStar())
                .numberOfReviews(product.getNumberOfReviews())
                .brandName(product.getBrand() != null ? product.getBrand().getName() : "")
                .brandImage(product.getBrand() != null ? product.getBrand().getImage() : "")
                .build();
    }
}