package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.convert.CartDetailConverter;
import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;
import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.dto.response.ProductResponse;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.repository.CartDetailRepository;
import hcmute.edu.vn.techstore.service.interfaces.ICartDetailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CartDetailServiceImpl implements ICartDetailService {
    private final CartDetailRepository cartDetailRepository;
    private final CartDetailConverter cartDetailConverter;

    List<CartDetailEntity> cartDetailEntitiesActived;
    List<CartDetailEntity> cartDetailEntitiesInactive;
    @Override
    public void getAllCartDetail(String email) {
        List<CartDetailEntity> cartDetailEntities = cartDetailRepository.findAllByCart_User_Account_Email(email);
        filterCartDetailByActived(cartDetailEntities);
    }

    @Override
    public List<CartDetailResponse> getAllCartDetailInactive() {
        return cartDetailEntitiesInactive.stream().map(cartDetailConverter::toCartDetailResponse).toList();
    }

    @Override
    public List<CartDetailResponse> getAllCartDetailActived() {
        return cartDetailEntitiesActived.stream().map(cartDetailConverter::toCartDetailResponse).toList();
    }

    public void filterCartDetailByActived(List<CartDetailEntity> cartDetailEntities) {
        for (CartDetailEntity cartDetailEntity : cartDetailEntities) {
            ProductEntity product = cartDetailEntity.getProduct();
            BrandEntity brand = product.getBrand();
            if (!product.isActived()||!brand.getIsActived()){
                cartDetailEntitiesInactive.add(cartDetailEntity);
            }
            else cartDetailEntitiesActived.add(cartDetailEntity);
        }
    }
}
