package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;

import java.math.BigDecimal;
import java.util.List;

public interface OrderPriceCalculator {
//    Đây là hợp đồng (interface) chung cho mọi "bộ tính giá". Ai muốn tính tổng tiền thì phải implement hàm
    BigDecimal calculateTotal(BigDecimal totalPrice, CheckoutRequest.DiscountCheckout discount);
}
