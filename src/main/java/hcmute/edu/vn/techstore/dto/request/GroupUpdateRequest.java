package hcmute.edu.vn.techstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupUpdateRequest {
    @Pattern(regexp = "^[a-zA-Z0-9 ]{3,50}$", message = "Name must be between 3 and 50 characters and contain only letters, numbers, and spaces")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;
}
