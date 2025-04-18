package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.dto.request.BrandRequest;
import hcmute.edu.vn.techstore.dto.response.BrandResponse;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.repository.BrandRepository;
import hcmute.edu.vn.techstore.repository.OrderDetailRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.service.interfaces.IBrandService;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements IBrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final IImageService imageService;

    @Override
    public Page<BrandResponse> findAll(Pageable pageable) {
        return brandRepository.findAll(pageable).map(brand -> BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .quantity(productRepository.countByBrand_Id(brand.getId()))
                .sale(orderDetailRepository.countAllByOrder_OrderStatusAndProduct_Brand_Id(EOrderStatus.DELIVERED_SUCCESSFULLY, brand.getId()))
                .image(brand.getImage())
                .isActive(brand.getIsActived())
                .build());
    }

    @Override
    public boolean addBrand(BrandRequest brandRequest, MultipartFile file) {
        try {
            BrandEntity brand = new BrandEntity();
            brand.setName(brandRequest.getBrandName());
            brand.setImage(imageService.saveImage(file));
            brand.setIsActived(true);
            brandRepository.save(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateBrand(BrandRequest brandRequest, Long id, MultipartFile file) {
        try {
            BrandEntity brand = brandRepository.findById(id).get();
            brand.setName(brandRequest.getBrandName());
            brand.setIsActived(brandRequest.isActive());
            if (file != null && !file.isEmpty()) {
                brand.setImage(imageService.updateImage(file, brand.getImage()));
            } else {
                brand.setImage(brand.getImage());
            }
            brandRepository.save(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteBrand(Long brandId) {
        try {
            BrandEntity brand = brandRepository.findById(brandId).get();
            brand.setIsActived(false);
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

    @Override
    public List<BrandEntity> findAllByIsActivedTrue() {
        return brandRepository.findAllByIsActived(true);
    }

    @Override
    public Optional<BrandEntity> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    public Optional<BrandResponse> findByIdResponse(Long aLong) {
        return brandRepository.findById(aLong).map(brand -> BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .quantity(productRepository.countByBrand_Id(brand.getId()))
                .sale(orderDetailRepository.countAllByOrder_OrderStatusAndProduct_Brand_Id(EOrderStatus.DELIVERED_SUCCESSFULLY, brand.getId()))
                .image(brand.getImage())
                .isActive(brand.getIsActived())
                .build());
    }

    @Override
    public Optional<BrandRequest> findByIdRequest(Long aLong) {
        return brandRepository.findById(aLong).map(brand -> BrandRequest.builder()
                .brandName(brand.getName())
                .brandImage(brand.getImage())
                .isActive(brand.getIsActived())
                .build());
    }
}
