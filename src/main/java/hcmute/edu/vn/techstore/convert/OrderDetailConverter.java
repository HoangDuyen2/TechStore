package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.dto.response.OrderDetailResponse;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.dto.response.ProductResponse;
import hcmute.edu.vn.techstore.entity.DiscountEntity;
import hcmute.edu.vn.techstore.entity.OrderDetailEntity;
import hcmute.edu.vn.techstore.entity.OrderEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderDetailConverter {
    private final PriceUtil priceUtil;
    private final ProductResponseConvert productResponseConvert;
    private final ModelMapper modelMapper;

    public OrderDetailResponse toOrderDetailResponse(OrderDetailEntity orderDetail) {
        ProductEntity productEntity = orderDetail.getProduct();
        OrderDetailResponse orderDetailResponse = modelMapper.map(orderDetail, OrderDetailResponse.class);
        orderDetailResponse.setProductResponse(productResponseConvert.toProductResponse(productEntity));
        BigDecimal price = productEntity.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
        orderDetailResponse.setTotalPrice(priceUtil.formatPrice(price));
        return orderDetailResponse;
    }
}