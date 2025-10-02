package hcmute.edu.vn.techstore.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompareProductDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private Boolean isActive = true;
}