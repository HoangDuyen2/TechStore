package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.dto.response.ProductResponse;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;

import hcmute.edu.vn.techstore.repository.BrandRepository;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductResponseConvert {
    private final ModelMapper modelMapper;
    private final PriceUtil priceUtil;
    private final BrandRepository brandRepository;

    public ProductResponse toProductResponse (ProductEntity product) {
        ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
        BrandEntity brand = brandRepository.findById(product.getBrand().getId()).orElse(null);
        if (brand != null) {
            productResponse.setBrandId(brand);
        }
        productResponse.setPrice(product.getPrice());
        return productResponse;
    }

//    public UserEntity toUserEntity (UserRequest userRequest) {
//        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);
//        userEntity.setId(userRequest.getUserId());
//        RoleEntity role = roleRepository.findByName(ERole.valueOf(userRequest.getRoleName())).orElse(null);
//        userEntity.setRole(role);
//        return userEntity;
//    }
}