package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;
import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.dto.response.ProductResponse;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CartDetailConverter {
    private final ModelMapper modelMapper;
    private final ProductResponseConvert productResponseConvert;
    private final PriceUtil priceUtil;

    public CartDetailResponse toCartDetailResponse(CartDetailEntity cartDetailEntity) {
        CartDetailResponse cartDetailResponse = modelMapper.map(cartDetailEntity, CartDetailResponse.class);
        cartDetailResponse.setCartId(cartDetailEntity.getId());
        cartDetailResponse.setProduct(productResponseConvert.toProductResponse(cartDetailEntity.getProduct()));
        BigDecimal totalPrice = getTotalPrice(cartDetailResponse);
        cartDetailResponse.setTotalPriceRow(priceUtil.formatPrice(totalPrice));
        return cartDetailResponse;
    }

    public BigDecimal getTotalPrice(CartDetailResponse cartDetailResponse) {
        BigDecimal totalPrice = BigDecimal.ZERO;
            ProductResponse productResponse = cartDetailResponse.getProduct();
            if (productResponse.isActived()){
                BigDecimal productItem = cartDetailResponse.getProduct().getPrice();
                int quantity = cartDetailResponse.getQuantity();
                totalPrice = totalPrice.add(productItem.multiply(new BigDecimal(quantity)));
            }
        return totalPrice;
    }

}
