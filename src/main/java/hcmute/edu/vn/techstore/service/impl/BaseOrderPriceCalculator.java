package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

public class BaseOrderPriceCalculator implements OrderPriceCalculator {
    private final PriceUtil priceUtil = new PriceUtil();

    @Override
    public BigDecimal calculateTotal(CheckoutRequest request) {
        return request.getProductCheckouts().stream()
                .map(product -> priceUtil.parsePrice(product.getPrice()).multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
