package hcmute.edu.vn.techstore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCompleteRespone {
    private Long orderId;
    private String orderDate;
    private String totalPrice;
    private String paymentMethod;
    private String address;
    private String phoneNumber;
}
