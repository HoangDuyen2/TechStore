package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.ProductImageUpdateDTO;
import hcmute.edu.vn.techstore.entity.ImageEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.repository.ImageRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import hcmute.edu.vn.techstore.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ImageUtil imageUtil;

    @Override
    public List<String> getImagePathsByProductId(Long productId) {
        return imageRepository.findByProductId(productId)
                .stream()
                .map(ImageEntity::getImagePath)
                .toList();
    }

    @Override
    @Transactional
    public void updateProductImages(ProductImageUpdateDTO form) {
        Long pid = form.getProductId();
        ProductEntity product = productRepository.findById(pid)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID " + pid));

        if (form.getKeptImagePaths() != null) {
            List<ImageEntity> allImages = imageRepository.findByProductId(pid);
            Set<String> keep = new HashSet<>(form.getKeptImagePaths());
            List<ImageEntity> toDelete = allImages.stream()
                    .filter(img -> !keep.contains(img.getImagePath()))
                    .toList();
            for (ImageEntity img : toDelete) {
                imageRepository.delete(img);
            }
        }

        MultipartFile[] files = form.getNewImages();
        if (files != null) {
            for (MultipartFile f : files) {
                if (f != null && !f.isEmpty()) {
                    String original = f.getOriginalFilename();
                    if (!imageUtil.isValidSuffixImage(original)) {
                        throw new IllegalArgumentException("Định dạng không hợp lệ: " + original);
                    }
                    try {
                        String url = imageUtil.saveImage(f);
                        ImageEntity newImg = ImageEntity.builder()
                                .imagePath(url)
                                .product(product)
                                .build();
                        imageRepository.save(newImg);
                    } catch (IOException e) {
                        throw new RuntimeException("Không lưu được ảnh " + original, e);
                    }
                }
            }
        }
    }

}
