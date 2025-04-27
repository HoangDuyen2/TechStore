package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;
import hcmute.edu.vn.techstore.dto.response.CartDetailWrapper;
import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.service.interfaces.ICartDetailService;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CartConverter {
    private final ICartDetailService cartDetailService;
    private final PriceUtil priceUtil;

    public CartResponse toCartResponse(CartEntity cartEntity) {
        CartResponse cartResponse = new CartResponse();
        CartDetailWrapper cartDetailWrapper = cartDetailService.getAllCartDetail(cartEntity.getUser().getAccount().getEmail());
        cartResponse.setCartDetails(cartDetailWrapper.getActiveCartDetails());
        return cartResponse;
    }
}
