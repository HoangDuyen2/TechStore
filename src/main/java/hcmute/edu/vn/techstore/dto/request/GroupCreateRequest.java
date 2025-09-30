package hcmute.edu.vn.techstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GroupCreateRequest {
    @Pattern(regexp = "^[a-zA-Z0-9 ]{3,50}$", message = "Group name must be between 3 and 50 characters and contain only letters, numbers, and spaces")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;
}
