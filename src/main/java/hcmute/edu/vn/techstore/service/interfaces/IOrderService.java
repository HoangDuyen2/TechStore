package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;

import java.util.List;

public interface IOrderService {
    CheckoutRequest getCheckoutRequest(String email);

    CheckoutRequest applyDiscount(CheckoutRequest checkoutRequest);

    boolean createOrder(CheckoutRequest checkoutRequest);
    boolean changeStatusOrder(Long orderId, EOrderStatus status);
    List<OrderResponse> getAllOrdersByUserEmail(String email);

}
