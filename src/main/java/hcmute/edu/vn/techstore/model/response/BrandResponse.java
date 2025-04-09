package hcmute.edu.vn.techstore.model.response;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandResponse {
    private Long id;
    private String name;
    private int quantity;
    private int sale;
    private String image;
}
