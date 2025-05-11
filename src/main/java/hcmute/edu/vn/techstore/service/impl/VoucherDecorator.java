package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;

import java.math.BigDecimal;
import java.util.List;

public class VoucherDecorator extends OrderPriceDecorator {
    public VoucherDecorator(OrderPriceCalculator wrapped) {
        super(wrapped);
    }

    @Override
    public BigDecimal calculateTotal(List<CheckoutRequest.ProductCheckout> products,
                                     CheckoutRequest.DiscountCheckout discountCheckout) {
        BigDecimal total = wrapped.calculateTotal(products, discountCheckout);
        BigDecimal fixedAmount = new BigDecimal(discountCheckout.getDiscountValue().replace(" VND", ""));
        return total.subtract(fixedAmount).max(BigDecimal.ZERO);
    }
}
