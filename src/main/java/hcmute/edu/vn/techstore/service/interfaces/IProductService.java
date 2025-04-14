package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IProductService {
    void saveProduct(ProductDTO product, MultipartFile file, String existingImagePath);
    ProductDTO findProductById(Long id);
}
