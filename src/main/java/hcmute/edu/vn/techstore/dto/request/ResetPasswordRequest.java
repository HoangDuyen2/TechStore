package hcmute.edu.vn.techstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    
    @NotBlank(message = "Please enter your password")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.@#$%^&+=])(?=\\S+$).{8,20}$", message = "Invalid password")
    private String password;

    @NotBlank(message = "Please enter your confirm password")
    private String confirmPassword;
    
    @NotBlank(message = "Token is required")
    private String token;
}
