package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;

import java.math.BigDecimal;
import java.util.List;

public abstract class OrderPriceDecorator implements OrderPriceCalculator {
    protected final OrderPriceCalculator wrapped;

    public OrderPriceDecorator(OrderPriceCalculator wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public abstract BigDecimal calculateTotal(List<CheckoutRequest.ProductCheckout> products,
                                              CheckoutRequest.DiscountCheckout discount);
}

