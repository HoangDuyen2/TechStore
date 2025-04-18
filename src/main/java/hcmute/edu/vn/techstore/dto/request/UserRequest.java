package hcmute.edu.vn.techstore.dto.request;

import hcmute.edu.vn.techstore.Enum.EGender;
import hcmute.edu.vn.techstore.dto.interfaces.ChangePassword;
import hcmute.edu.vn.techstore.dto.interfaces.OnCreate;
import hcmute.edu.vn.techstore.dto.interfaces.OnUpdate;
import hcmute.edu.vn.techstore.dto.interfaces.StaffGroup;
import hcmute.edu.vn.techstore.validation.ValidDateOfBirth;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Transient;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private Long userId;

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

    @NotBlank(message = "Please enter your Phone number!")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Phone number should be in format like '0437463847' or '034-937-9472' or '042 483 4837'")
    private String phoneNumber;

    @NotBlank(message = "Please enter your firstname!")
    private String firstName;

    @NotBlank(message = "Please enter your lastname!")
    private String lastName;

    private String address;

    private EGender gender;

    @ValidDateOfBirth(message = "Date of birth must be in the past and age must be between 18 and 100")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private MultipartFile image;

    private String roleName;

    @Pattern(regexp = "^\\d{12}$", message = "CCCD must have 12-digit numbers", groups = StaffGroup.class)
    private String cccd;

    @Length(max = 500, groups = StaffGroup.class)
    private String relativeName;

    @Pattern(
            regexp = "^$|^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Relative number should be in format like '0437463847' or '034-937-9472'",
            groups = StaffGroup.class
    )
    private String relativePhoneNumber;
}