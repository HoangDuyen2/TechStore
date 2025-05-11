package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class BaseOrderPriceCalculator implements OrderPriceCalculator {
    private final PriceUtil priceUtil = new PriceUtil();

    @Override
    public BigDecimal calculateTotal(BigDecimal totalPrice,
                                     CheckoutRequest.DiscountCheckout discountCheckout) {
        return totalPrice;
    }
}
