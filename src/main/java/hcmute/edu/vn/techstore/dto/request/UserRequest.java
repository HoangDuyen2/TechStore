package hcmute.edu.vn.techstore.dto.request;

import hcmute.edu.vn.techstore.Enum.EGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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

    @NotBlank(message = "Please enter your email!")
    @Email(message = "Email bắt buộc chứa kí tự @.\n Ex: name@gmail.com")
    private String email;

    private String password;

    private String confirmPassword;

    @NotBlank(message = "Please enter your Phone number!")
    private String phoneNumber;

    @NotBlank(message = "Please enter your firstname!")
    private String firstName;

    @NotBlank(message = "Please enter your lastname!")
    private String lastName;

    private String address;

    private EGender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private MultipartFile image;

    private String roleName;

    private String cccd;

    private String relativeName;

    private String relativePhoneNumber;
}