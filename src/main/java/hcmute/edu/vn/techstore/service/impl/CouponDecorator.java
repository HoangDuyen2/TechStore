package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;

import java.math.BigDecimal;
import java.util.List;

public class CouponDecorator extends OrderPriceDecorator {
    public CouponDecorator(OrderPriceCalculator wrapped) {
        super(wrapped);
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal totalPrice,
                                     CheckoutRequest.DiscountCheckout discountCheckout) {
        BigDecimal total = wrapped.calculateTotal(totalPrice, discountCheckout);
        BigDecimal percent = new BigDecimal(discountCheckout.getDiscountValue().replace("%", ""));
        BigDecimal discount = total.multiply(percent).divide(BigDecimal.valueOf(100));
        return total.subtract(discount).max(BigDecimal.ZERO);
    }
}
