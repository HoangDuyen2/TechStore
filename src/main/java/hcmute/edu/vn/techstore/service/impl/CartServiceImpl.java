package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.convert.CartConverter;
import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;
import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.dto.response.ProductResponse;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.CartRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.ICartDetailService;
import hcmute.edu.vn.techstore.service.interfaces.ICartService;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements ICartService {
    private final CartRepository cartRepository;
    private final CartConverter cartConverter;
    private final UserRepository userRepository;
    private final ICartDetailService cartDetailService;
    private final PriceUtil priceUtil;

    @Override
    public CartResponse getCart(String email) {
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(email).orElse(null);

        if (cartEntity == null) {
            return null;
        }
        CartResponse cartResponse = cartConverter.toCartResponseActived(cartEntity);
        cartResponse.setTotalPrice(priceUtil.formatPrice(totalPrice(cartResponse)));
        cartEntity.setTotalPrice(totalPrice(cartResponse));
        cartRepository.save(cartEntity);
        return cartResponse;
    }

    public CartResponse getAllCartDetailInactive(String email) {
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(email).orElse(null);

        if (cartEntity == null) {
            return null;
        }
        CartResponse cartResponse = cartConverter.toCartResponseInActived(cartEntity);
        cartResponse.setTotalPrice(priceUtil.formatPrice(totalPrice(cartResponse)));
        return cartResponse;
    }

    @Override
    public void addCart(Long productId, String email) {
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(email).orElse(null);
        if (cartEntity == null) {
            UserEntity user = userRepository.findByAccount_Email(email).orElse(null);
            cartEntity = CartEntity.builder()
                    .totalPrice(BigDecimal.ZERO)
                    .build();
            cartRepository.save(cartEntity);
            user.setCart(cartEntity);
            userRepository.save(user);
        }
        if (cartEntity != null) {
            cartDetailService.addCartDetail(productId, cartEntity.getId());
        }
    }

    public BigDecimal totalPrice(CartResponse cartResponse) {
        List<CartDetailResponse> cartDetailResponseList = cartResponse.getCartDetails();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartDetailResponse cartDetailResponse : cartDetailResponseList) {
            totalPrice = totalPrice.add(priceUtil.parsePrice(cartDetailResponse.getTotalPriceRow()));
        }
        return totalPrice;
    }

    public Long getCartId(String email) {
        Optional<CartEntity> cartEntity = Optional.ofNullable(cartRepository.findByCart_User_Account_Email(email).orElse(null));
        return cartEntity.map(CartEntity::getId).orElse(null);
    }

    public void deleteAllCartDetails(String email){
        cartDetailService.deleteAllCartDetail(email);
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(email).orElse(null);
        cartEntity.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cartEntity);
    }

}
