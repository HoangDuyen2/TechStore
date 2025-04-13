package hcmute.edu.vn.techstore.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Code cannot be blank")
    private String code;

    @NotNull(message = "Value cannot be null")
    @Min(value = 1, message = "Value must be at least 1")
    @Max(value = 100, message = "Value must be at most 100")
    private Integer discountPercent;

    @NotNull(message = "Expired date cannot be null")
    @Future(message = "Expired date must be in the future")
    private LocalDate expiredDate;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
