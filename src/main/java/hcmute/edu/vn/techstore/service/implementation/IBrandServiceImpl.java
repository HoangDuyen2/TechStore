package hcmute.edu.vn.techstore.service.implementation;

import hcmute.edu.vn.techstore.dto.request.BrandRequest;
import hcmute.edu.vn.techstore.dto.response.BrandResponse;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.repository.BrandRepository;
import hcmute.edu.vn.techstore.repository.OrderDetailRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.service.IBrandService;
import hcmute.edu.vn.techstore.service.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class IBrandServiceImpl implements IBrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private IImageService imageService;

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

    @Override
    public boolean addBrand(BrandRequest brandRequest, MultipartFile file){
        try {
            Long imageId = imageService.addImage(file);
            if (imageId == null)
                return false;
            BrandEntity brand = new BrandEntity();
            brand.setName(brandRequest.getBrandName());
            brand.setImage(imageService.findById(imageId));
            brandRepository.save(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public BrandEntity findByName(String name) {
        return brandRepository.findByName(name);
    }
}
