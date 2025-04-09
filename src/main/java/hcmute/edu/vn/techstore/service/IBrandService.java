package hcmute.edu.vn.techstore.service;

import hcmute.edu.vn.techstore.dto.request.BrandRequest;
import hcmute.edu.vn.techstore.dto.response.BrandResponse;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IBrandService {
    Page<BrandResponse> findAll(Pageable pageable);

    boolean addBrand(BrandRequest brandRequest, MultipartFile file);

    BrandEntity findByName(String name);
}
