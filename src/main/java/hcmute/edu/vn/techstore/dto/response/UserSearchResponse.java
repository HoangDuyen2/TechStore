package hcmute.edu.vn.techstore.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSearchResponse {
    private Long id;
    private String email;
    private String phone;
    private String fullName;
}
