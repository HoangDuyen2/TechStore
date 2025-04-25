package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.convert.CartConverter;
import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;
import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.dto.response.ProductResponse;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.repository.CartRepository;
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
    private final PriceUtil priceUtil;

    @Override
    public CartResponse getCart(String email) {
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(email).orElse(null);

        if (cartEntity == null) {
            return null;
        }
        CartResponse cartResponse = cartConverter.toCartResponse(cartEntity);
        cartResponse.setTotalPrice(priceUtil.formatPrice(totalPrice(cartResponse)));
        return cartResponse;
    }

    public BigDecimal totalPrice(CartResponse cartResponse) {
        List<CartDetailResponse> cartDetailResponseList = cartResponse.getCartDetails();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartDetailResponse cartDetailResponse : cartDetailResponseList) {
            totalPrice = totalPrice.add(priceUtil.parsePrice(cartDetailResponse.getTotalPriceRow()));
        }
        return totalPrice;
    }

}
