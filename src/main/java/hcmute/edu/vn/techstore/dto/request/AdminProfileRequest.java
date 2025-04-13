package hcmute.edu.vn.techstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminProfileRequest {
    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String image;

    @Email(message = "Email should be valid")
    private String email;

    private String password;

    private String confirmPassword;
}
