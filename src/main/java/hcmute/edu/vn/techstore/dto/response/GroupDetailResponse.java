package hcmute.edu.vn.techstore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupDetailResponse {
    private Long id;
    private String name;
    private String description;
    private List<UserSearchResponse> users;
}
