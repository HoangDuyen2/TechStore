package hcmute.edu.vn.techstore.service.implementation;

import hcmute.edu.vn.techstore.dto.BrandResponse;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.repository.BrandRepository;
import hcmute.edu.vn.techstore.repository.OrderDetailRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IBrandServiceImpl implements IBrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public Page<BrandResponse> findAll(Pageable pageable) {
        return brandRepository.findAll(pageable).map(brand -> BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .quantity(productRepository.countByBrand_Id(brand.getId()))
                .sale(orderDetailRepository.countAllByProduct_Brand_Id(brand.getId()))
                .image(brand.getImage().getImagePath())
                .build());
    }
}
