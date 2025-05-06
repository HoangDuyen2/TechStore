package hcmute.edu.vn.techstore.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewRequest {
    @NotNull
    private Long orderId;

    @NotNull
    private Long productId;

    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank
    @Size(max = 1000)
    private String comment;
}
