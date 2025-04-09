package hcmute.edu.vn.techstore.service;

import hcmute.edu.vn.techstore.entity.ImageEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    Long addImage(MultipartFile file);

    ImageEntity findById(Long aLong);
}
