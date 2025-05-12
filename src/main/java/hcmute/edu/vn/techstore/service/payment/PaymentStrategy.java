package hcmute.edu.vn.techstore.service.payment;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;

public interface PaymentStrategy {
    String processPayment(CheckoutRequest checkoutRequest);
} 