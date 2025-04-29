package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;

public abstract class OrderPriceDecorator implements OrderPriceCalculator {
    protected OrderPriceCalculator wrapped;

    public OrderPriceDecorator(OrderPriceCalculator wrapped) {
        this.wrapped = wrapped;
    }
}

