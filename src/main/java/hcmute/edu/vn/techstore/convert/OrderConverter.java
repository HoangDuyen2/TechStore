package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.dto.response.OrderDetailResponse;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.entity.DiscountEntity;
import hcmute.edu.vn.techstore.entity.OrderDetailEntity;
import hcmute.edu.vn.techstore.entity.OrderEntity;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderConverter {
    private final PriceUtil priceUtil;
    private final ModelMapper modelMapper;
    private final OrderDetailConverter orderDetailConverter;

    public OrderResponse toResponse(OrderEntity order) {
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        DiscountEntity discount = order.getDiscount();
        List<OrderDetailEntity> orderDetails = order.getOrderDetails();
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        orderResponse.setTotalPrice(priceUtil.formatPrice(order.getTotalPrice()));
        orderResponse.setCustomerName(order.getUser().getLastName() + " " + order.getUser().getFirstName());
        orderResponse.setPaymentName(order.getPayment().getName());
        if (discount != null) {
            orderResponse.setDiscountName(discount.getName());
            orderResponse.setDiscountType(discount.getDiscountType());
            orderResponse.setDiscountCode(discount.getCode());
        }
        orderResponse.setOrderDate(order.getOrderDate().toLocalDate());
        for (OrderDetailEntity orderDetail : orderDetails) {
            orderDetailResponses.add(orderDetailConverter.toOrderDetailResponse(orderDetail));
        }
        orderResponse.setOrderDetails(orderDetailResponses);
        return orderResponse;
    }
}