package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.response.*;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.repository.CartDetailRepository;
import hcmute.edu.vn.techstore.repository.CartRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.service.interfaces.ICartDetailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CartDetailServiceImpl implements ICartDetailService {
    private final CartDetailFactory cartDetailFactory;
    private final CartDetailRepository cartDetailRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    public void addCartDetail(Long productId, CartEntity cartEntity) {
        CartDetailEntity cartDetailEntity = null;
        if (cartEntity.getCartDetails() != null) {
            for (CartDetailEntity cartDetail : cartEntity.getCartDetails()) {
                if (cartDetail.getProduct().getId().equals(productId)) {
                    cartDetailEntity = cartDetail;
                    break;
                }
            }
        }
        else cartEntity.setCartDetails(new ArrayList<>());
        ProductEntity product = productRepository.findById(productId).orElse(null);
        if (cartDetailEntity == null) {
            cartDetailEntity = new CartDetailEntity();
            cartDetailEntity.setQuantity(1);
            cartDetailEntity.setProduct(product);
        }
        else cartDetailEntity.setQuantity(cartDetailEntity.getQuantity()+1);
        cartEntity.getCartDetails().add(cartDetailEntity);
        cartRepository.save(cartEntity);
    }

    @Override
    public void deleteCartDetail(Long productId, CartEntity cartEntity) {
        CartDetailEntity cartDetailEntity = null;
        for (CartDetailEntity cartDetail : cartEntity.getCartDetails()) {
            if (cartDetail.getProduct().getId().equals(productId)) {
                cartDetailEntity = cartDetail;
                break;
            }
        }
        if (cartDetailEntity == null) {
            throw new RuntimeException("Not found this product");
        }
        cartEntity.getCartDetails().remove(cartDetailEntity);
        cartRepository.save(cartEntity);
    }

    @Override
    public void decreaseCartDetail(Long productId, CartEntity cartEntity) {
        CartDetailEntity cartDetailEntity = null;
        for (CartDetailEntity cartDetail : cartEntity.getCartDetails()) {
            if (cartDetail.getProduct().getId().equals(productId)) {
                cartDetailEntity = cartDetail;
                break;
            }
        }
        if (cartDetailEntity == null) {
            throw new RuntimeException("Not found this product");
        }
        else cartDetailEntity.setQuantity(cartDetailEntity.getQuantity()-1);
        cartRepository.save(cartEntity);
    }

    @Override
    public void deleteAllCartDetail(CartEntity cartEntity){
        List<CartDetailEntity> cartDetailEntities = cartEntity.getCartDetails();
        cartDetailRepository.deleteAll(cartDetailEntities);
    }

}
