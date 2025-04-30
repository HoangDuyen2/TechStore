package hcmute.edu.vn.techstore.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductImageUpdateDTO {
    private Long productId;

    private List<String> existingImagePaths = new ArrayList<>();

    private List<String> keptImagePaths = new ArrayList<>();

    private MultipartFile[] newImages;
}
