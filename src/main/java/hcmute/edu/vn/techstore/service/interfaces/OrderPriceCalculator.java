package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;

import java.math.BigDecimal;
import java.util.List;

public interface OrderPriceCalculator {
    BigDecimal calculateTotal(BigDecimal totalPrice, CheckoutRequest.DiscountCheckout discount);
}
