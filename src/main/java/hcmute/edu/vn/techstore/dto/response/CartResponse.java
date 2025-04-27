package hcmute.edu.vn.techstore.dto.response;

import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private List<CartDetailResponse> cartDetails;

    private String totalPrice;
}
