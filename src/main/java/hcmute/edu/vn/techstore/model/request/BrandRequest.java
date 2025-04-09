package hcmute.edu.vn.techstore.model.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandRequest {
    private String brandName;
    private String brandImage;
}
