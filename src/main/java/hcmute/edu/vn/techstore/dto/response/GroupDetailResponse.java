package hcmute.edu.vn.techstore.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String createdAt;
    private String updatedAt;
}
