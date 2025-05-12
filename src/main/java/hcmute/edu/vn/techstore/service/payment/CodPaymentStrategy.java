package hcmute.edu.vn.techstore.service.payment;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import org.springframework.stereotype.Component;

@Component
public class CodPaymentStrategy implements PaymentStrategy {
    @Override
    public String processPayment(CheckoutRequest checkoutRequest) {
        // For COD, we just return success as payment will be collected on delivery
        return "success";
    }
} 