package hcmute.edu.vn.techstore.service.payment;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import org.springframework.stereotype.Component;

@Component
public class VnPayPaymentStrategy implements PaymentStrategy {
    @Override
    public String processPayment(CheckoutRequest checkoutRequest) {
        // TODO: Implement VNPay payment integration
        return "vnpay_payment_url";
    }
} 