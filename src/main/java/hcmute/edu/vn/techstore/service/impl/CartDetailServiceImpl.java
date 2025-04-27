package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.convert.CartDetailConverter;
import hcmute.edu.vn.techstore.dto.response.*;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.repository.CartDetailRepository;
import hcmute.edu.vn.techstore.repository.CartRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.service.interfaces.ICartDetailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CartDetailServiceImpl implements ICartDetailService {
    private final CartDetailFactory cartDetailFactory;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    @Override
    public CartDetailWrapper getAllCartDetail(String email) {
        List<CartDetailEntity> cartDetailEntities = cartDetailRepository.findAllByCart_User_Account_Email(email);

        List<CartDetailEntity> cartDetailEntitiesActived = new ArrayList<>();
        List<CartDetailEntity> cartDetailEntitiesInactive = new ArrayList<>();

        for (CartDetailEntity cartDetailEntity : cartDetailEntities) {
            ProductEntity product = cartDetailEntity.getProduct();
            if (!product.isActived() || !product.getBrand().getIsActived()) {
                cartDetailEntitiesInactive.add(cartDetailEntity);
            } else {
                cartDetailEntitiesActived.add(cartDetailEntity);
            }
        }

        List<CartDetailResponse> activeResponses = cartDetailFactory.createCartDetailResponse(cartDetailEntitiesActived);
        List<CartDetailResponse> inactiveResponses = cartDetailFactory.createCartDetailResponse(cartDetailEntitiesInactive);

        return new CartDetailWrapper(activeResponses, inactiveResponses);
    }



    @Override
    public void addCartDetail(Long productId, Long cartId) {
        CartDetailEntity cartDetailEntity = cartDetailRepository.findByCart_IdAndAndProduct_Id(cartId, productId);
        CartEntity cartEntity = cartRepository.findById(cartId).orElse(null);
        ProductEntity product = productRepository.findById(productId).orElse(null);
        if (cartDetailEntity == null) {
            cartDetailEntity = new CartDetailEntity();
            cartDetailEntity.setQuantity(1);
            cartDetailEntity.setProduct(product);
            cartDetailEntity.setCart(cartEntity);
        }
        else cartDetailEntity.setQuantity(cartDetailEntity.getQuantity()+1);
        cartDetailRepository.save(cartDetailEntity);
    }
}
