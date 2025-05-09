package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.dto.response.OrderCompleteRespone;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;

import java.util.List;

public interface IOrderService {
    CheckoutRequest getCheckoutRequest(String email, List<Long> selectedProductIds);

    CheckoutRequest applyDiscount(CheckoutRequest checkoutRequest);

    Long createOrder(CheckoutRequest checkoutRequest);
    boolean changeStatusOrder(Long orderId, EOrderStatus status);
    List<OrderResponse> getAllOrdersByUserEmail(String email);

    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long orderId);

    OrderCompleteRespone getOrderCompleteResponse(Long orderId);
    boolean updateOrderAddress(Long orderId, String address);
    List<OrderResponse> getOrdersByStatus(EOrderStatus status);

    String getTotalPurchaseDue();

    String getTotalProductsSold();
}
