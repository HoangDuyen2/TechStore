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
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OrderConverter {
    private final PriceUtil priceUtil;
    private final ModelMapper modelMapper;
    private final OrderDetailConverter orderDetailConverter;

    public OrderResponse toResponse(OrderEntity order) {
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        Set<DiscountEntity> discounts = order.getDiscounts();
        List<OrderDetailEntity> orderDetails = order.getOrderDetails();
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        orderResponse.setTotalPrice(priceUtil.formatPrice(order.getTotalPrice()));
        orderResponse.setCustomerName(order.getUser().getLastName() + " " + order.getUser().getFirstName());
        orderResponse.setPaymentName(order.getPayment().getName());
        List<OrderResponse.DiscountOrderResponse> discountOrderResponses = new ArrayList<>();
        for (DiscountEntity discount : discounts) {
            OrderResponse.DiscountOrderResponse discountResponse = new OrderResponse.DiscountOrderResponse();
            discountResponse.setDiscountName(discount.getName());
            discountResponse.setDiscountCode(discount.getCode());
            discountResponse.setDiscountType(discount.getDiscountType());
            discountOrderResponses.add(discountResponse);
        }
        orderResponse.setDiscounts(discountOrderResponses);
        orderResponse.setOrderDate(order.getOrderDate().toLocalDate());
        for (OrderDetailEntity orderDetail : orderDetails) {
            orderDetailResponses.add(orderDetailConverter.toOrderDetailResponse(orderDetail));
        }
        orderResponse.setOrderDetails(orderDetailResponses);
        return orderResponse;
    }
}