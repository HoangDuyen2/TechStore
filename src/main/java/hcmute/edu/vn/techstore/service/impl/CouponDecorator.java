package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;

import java.math.BigDecimal;

public class CouponDecorator extends OrderPriceDecorator {
    public CouponDecorator(OrderPriceCalculator wrapped) {
        super(wrapped);
    }

    @Override
    public BigDecimal calculateTotal(CheckoutRequest request) {
        BigDecimal total = wrapped.calculateTotal(request);
        BigDecimal percent = new BigDecimal(request.getDiscountValue().replace("%", "")); // e.g., 10 means 10%
        BigDecimal discount = total.multiply(percent).divide(BigDecimal.valueOf(100));
        return total.subtract(discount).max(BigDecimal.ZERO);
    }
}
