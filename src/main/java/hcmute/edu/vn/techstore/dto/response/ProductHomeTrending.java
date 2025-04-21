package hcmute.edu.vn.techstore.dto.response;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductHomeTrending {
    private Long id;
    private String name;
    private String thumbnail;
    private String price;
    private int stars;
    private String description;
}
