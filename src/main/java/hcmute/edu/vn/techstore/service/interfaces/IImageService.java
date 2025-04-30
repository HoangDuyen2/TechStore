package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.ProductImageUpdateDTO;

import java.util.List;

public interface IImageService {
    List<String> getImagePathsByProductId(Long productId);
    void updateProductImages(ProductImageUpdateDTO form);
}
