package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.BrandRequest;
import hcmute.edu.vn.techstore.dto.response.BrandResponse;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface IBrandService {
    Page<BrandResponse> findAll(Pageable pageable);

    boolean addBrand(BrandRequest brandRequest, MultipartFile file);

    boolean updateBrand(BrandRequest brandRequest, Long id);

    boolean updateBrand(BrandRequest brandRequest, Long id, MultipartFile file);

    boolean deleteBrand(Long brandId);

    BrandEntity findByName(String name);

    Optional<BrandEntity> findById(Long id);

    Optional<BrandResponse> findByIdResponse(Long aLong);

    Optional<BrandRequest> findByIdRequest(Long aLong);
}
