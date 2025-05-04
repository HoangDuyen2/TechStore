package hcmute.edu.vn.techstore.dto.response;

import hcmute.edu.vn.techstore.Enum.EDiscountType;
import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.entity.OrderDetailEntity;
import hcmute.edu.vn.techstore.entity.PaymentEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private LocalDate orderDate;
    private String address;
    private EOrderStatus orderStatus;
    private String totalPrice;
    private String discountName;
    private String discountCode;
    private EDiscountType discountType;
    private String paymentName;
    private List<OrderDetailResponse> orderDetails;

}