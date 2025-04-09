package hcmute.edu.vn.techstore.service;

import hcmute.edu.vn.techstore.dto.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBrandService {
    Page<BrandResponse> findAll(Pageable pageable);
}
