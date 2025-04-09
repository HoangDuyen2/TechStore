package hcmute.edu.vn.techstore.service.implementation;

import hcmute.edu.vn.techstore.entity.ImageEntity;
import hcmute.edu.vn.techstore.repository.ImageRepository;
import hcmute.edu.vn.techstore.service.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ImageServiceImpl implements IImageService {
    @Autowired
    private ImageRepository imageRepository;
    private final Path root = Paths.get("./uploads");

    @Override
    public Long addImage(MultipartFile file) {
        try {
            String img = "";
            if (file == null) {
                img = "/uploads/default.jpg";
            } else {
                if (!isValidSuffixImage(file.getOriginalFilename())) {
                    return null;
                }
                img = saveImage(file);
            }
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagePath(img);
            imageRepository.save(imageEntity);
            return imageEntity.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ImageEntity findById(Long aLong) {
        return imageRepository.findById(aLong).orElse(null);
    }

    @Override
    public String saveImage(MultipartFile file) {
        try {
            // get file name
            String fileName = file.getOriginalFilename();
            // generate code random base on UUID
            String uniqueFileName = UUID.randomUUID() + "_" + LocalDate.now() + "_" + fileName;
            Files.copy(file.getInputStream(), this.root.resolve(uniqueFileName), StandardCopyOption.REPLACE_EXISTING);
            return uniqueFileName;
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("Filename already exists.");
            }

            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean isValidSuffixImage(String img) {
        return img.endsWith(".jpg") || img.endsWith(".jpeg") ||
                img.endsWith(".png") || img.endsWith(".gif") ||
                img.endsWith(".bmp");
    }

    @Override
    public String updateImage(MultipartFile file, String filename) {
        try {
            if (deleteImage(filename)) {
                return saveImage(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveImage(file);
    }

    @Override
    public boolean deleteImage(String filename) {
        try {
            Path file = root.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
