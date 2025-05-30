package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.ProductDTO;
import hcmute.edu.vn.techstore.dto.response.*;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IProductService {
    void saveProduct(ProductDTO product, MultipartFile file, String existingImagePath);

    ProductDTO findProductById(Long id);

    List<ProductDTO> findAllProduct();

    Page<ProductCollectionResponse> filterProducts(Map<String, Object> params, Pageable pageable);

    List<ProductHomeSlider> getProductHomeSlider();

    List<ProductHomeTrending> getProductHomeTrending();
    Optional<ProductResponse> findProductResponseById(Long id);

    List<ProductSearchSuggestion> searchSuggestions(String keyword);

    boolean decreaseQuantity(Long id, int quantity);

    Optional<ProductEntity> findById(Long aLong);

    String getTotalAvailableProducts();

    List<AdminDashboardTopSellingProduct> getTopSellingProducts();
}