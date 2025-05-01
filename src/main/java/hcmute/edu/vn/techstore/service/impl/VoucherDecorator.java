package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;

import java.math.BigDecimal;

public class VoucherDecorator extends OrderPriceDecorator {
    public VoucherDecorator(OrderPriceCalculator wrapped) {
        super(wrapped);
    }

    @Override
    public BigDecimal calculateTotal(CheckoutRequest request) {
        BigDecimal total = wrapped.calculateTotal(request);
        BigDecimal fixedAmount = new BigDecimal(request.getDiscountValue().replace(" VND", "")); // e.g., 100000 means 100,000 VND
        return total.subtract(fixedAmount).max(BigDecimal.ZERO);
    }
}
