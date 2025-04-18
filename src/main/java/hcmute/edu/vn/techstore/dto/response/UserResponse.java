package hcmute.edu.vn.techstore.dto.response;

import hcmute.edu.vn.techstore.Enum.EGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long userId;

    @NotBlank(message = "Please enter your email!")
    @Email(message = "Email bắt buộc chứa kí tự @.\n Ex: name@gmail.com")
    private String email;

    @NotBlank(message = "Please enter your Phone number!")
    private String phoneNumber;

    private String fullName;

    private EGender gender;

    @NotNull(message = "Please enter your date of birth!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String image;

    private String roleName;

    private String address;

    private boolean isActived;
}
