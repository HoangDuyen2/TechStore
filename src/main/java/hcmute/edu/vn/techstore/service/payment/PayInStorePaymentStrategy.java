package hcmute.edu.vn.techstore.service.payment;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import org.springframework.stereotype.Component;

@Component
public class PayInStorePaymentStrategy implements PaymentStrategy {
    @Override
    public String processPayment(CheckoutRequest checkoutRequest) {
        // For Pay In Store, we just return success as payment will be collected in store
        return "success";
    }
} 