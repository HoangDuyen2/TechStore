package hcmute.edu.vn.techstore.dto.response;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewHomePage {
    private String comment;
    private String customerAvatar;
    private String customerName;
    private int star;
}
