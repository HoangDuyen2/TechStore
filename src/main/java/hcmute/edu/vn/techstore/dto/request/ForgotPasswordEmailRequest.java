package hcmute.edu.vn.techstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordEmailRequest {
    
    @NotBlank(message = "Please enter your email!")
    @Email(message = "Email must have @ and .")
    private String email;
}
