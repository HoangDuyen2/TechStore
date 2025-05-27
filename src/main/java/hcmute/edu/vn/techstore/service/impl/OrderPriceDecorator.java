package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;

import java.math.BigDecimal;
import java.util.List;

public abstract class OrderPriceDecorator implements OrderPriceCalculator {
    protected final OrderPriceCalculator wrapped; // Cho phép bạn gọi lại wrapped.calculateTotal(...) để áp các giảm giá lồng nhau nếu muốn.

    public OrderPriceDecorator(OrderPriceCalculator wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public abstract BigDecimal calculateTotal(BigDecimal totalPrice,
                                              CheckoutRequest.DiscountCheckout discount);
}

