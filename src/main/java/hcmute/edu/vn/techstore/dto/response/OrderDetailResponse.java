package hcmute.edu.vn.techstore.dto.response;

import hcmute.edu.vn.techstore.entity.OrderEntity;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long orderId;
    private ProductResponse productResponse;
    private int quantity;
    private String totalPrice;
}
