package hcmute.edu.vn.techstore.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountResponse {
    private Long id;
    private String name;
    private String code;
    private String type;
    private Long amount;
    private LocalDate expiredDate;
    private int quantity;
    private int usedQuantity;
}
