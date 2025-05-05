package hcmute.edu.vn.techstore.dto.request;

import hcmute.edu.vn.techstore.dto.interfaces.ChangePassword;
import hcmute.edu.vn.techstore.dto.interfaces.OnCreate;
import hcmute.edu.vn.techstore.dto.interfaces.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "Please enter your email!", groups = ChangePassword.class)
    @Pattern(regexp = "(?i)[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}", message = "Email must have @ and .")
    private String email;

    @NotBlank(groups = {OnCreate.class, ChangePassword.class}, message = "Please enter your password")
    @Null(groups = OnUpdate.class)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.@#$%^&+=])(?=\\S+$).{8,20}$", message = "Invalid password")
    private String password;

    @NotBlank(groups = {OnCreate.class, ChangePassword.class}, message = "Please enter your confirm password")
    @Null(groups = OnUpdate.class)
    private String confirmPassword;
}
