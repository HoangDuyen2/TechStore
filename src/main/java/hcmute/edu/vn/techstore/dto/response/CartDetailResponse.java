package hcmute.edu.vn.techstore.dto.response;

import hcmute.edu.vn.techstore.dto.ProductDTO;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDetailResponse {
    private Long cartId;

    private ProductResponse product;

    private int quantity;

    private String totalPriceRow;
}
