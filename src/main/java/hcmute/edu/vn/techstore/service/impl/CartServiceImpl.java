package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.convert.CartDetailConverter;
import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;
import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.CartRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.ICartDetailService;
import hcmute.edu.vn.techstore.service.interfaces.ICartService;
import hcmute.edu.vn.techstore.service.interfaces.ICartSplitterFactory;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartServiceImpl implements ICartService {
    private final CartRepository cartRepository;
    private final CartDetailConverter cartDetailConverter;
    private final UserRepository userRepository;
    private final ICartDetailService cartDetailService;
    private final ICartSplitterFactory splitterFactory;
    private final PriceUtil priceUtil;

    @Override
    public CartResponse getCart(String email) {
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(email).orElse(null);

        if (cartEntity == null) {
            return null;
        }
        Map<String, List<CartDetailEntity>> groups = splitterFactory
                .getSplitter("activeInactiveSplitter")
                .split(cartEntity.getCartDetails());
        List<CartDetailResponse> active = groups.get("active").stream()
                .map(cartDetailConverter::toCartDetailResponse)
                .collect(Collectors.toList());
        List<CartDetailResponse> inactive = groups.get("inactive").stream()
                .map(cartDetailConverter::toCartDetailResponse)
                .collect(Collectors.toList());
        CartResponse cartResponse = CartResponse
                .builder()
                .activeDetails(active)
                .inactiveDetails(inactive)
                .build();
        cartResponse.setTotalPrice(priceUtil.formatPrice(totalPrice(cartResponse)));
        cartEntity.setTotalPrice(totalPrice(cartResponse));
        cartRepository.save(cartEntity);
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
            cartDetailService.addCartDetail(productId, cartEntity);
        }
    }

    public BigDecimal totalPrice(CartResponse cartResponse) {
        List<CartDetailResponse> cartDetailResponseList = cartResponse.getActiveDetails();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartDetailResponse cartDetailResponse : cartDetailResponseList) {
            totalPrice = totalPrice.add(priceUtil.parsePrice(cartDetailResponse.getTotalPriceRow()));
        }
        return totalPrice;
    }

    public void deleteAllCartDetails(String email){
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(email).orElse(null);
        cartDetailService.deleteAllCartDetail(cartEntity);
        cartEntity.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cartEntity);
    }
    public CartEntity getCartEntity(String email) {
        return cartRepository.findByCart_User_Account_Email(email).orElse(null);
    }

}
