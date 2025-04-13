package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.entity.ImageEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    String addImage(MultipartFile file);

    ImageEntity findById(Long aLong);

    String saveImage(MultipartFile file);

    String updateImage(MultipartFile file, String filename);

    boolean deleteImage(String filename);
}
