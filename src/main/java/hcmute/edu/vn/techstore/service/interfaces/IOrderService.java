package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;

public interface IOrderService {
    CheckoutRequest getCheckoutRequest(String email);

    CheckoutRequest applyDiscount(CheckoutRequest checkoutRequest);

    boolean createOrder(CheckoutRequest checkoutRequest);
}
