package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;

import java.math.BigDecimal;

public interface OrderPriceCalculator {
    BigDecimal calculateTotal(CheckoutRequest request);
}
