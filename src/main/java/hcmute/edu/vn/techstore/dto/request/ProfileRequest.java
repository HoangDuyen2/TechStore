package hcmute.edu.vn.techstore.dto.request;

import hcmute.edu.vn.techstore.Enum.EGender;
import hcmute.edu.vn.techstore.validation.ValidDateOfBirth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Setter
@Getter
public class ProfileRequest {
    @NotBlank(message = "Please enter your firstname!")
    private String firstName;

    @NotBlank(message = "Please enter your lastname!")
    private String lastName;

    @NotBlank(message = "Please enter your Phone number!")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Phone number should be in format like '0437463847' or '034-937-9472' or '042 483 4837'")
    private String phoneNumber;

    @NotBlank(message = "Please enter your address!")
    private String address;

    private EGender gender;

    @ValidDateOfBirth(message = "Date of birth must be in the past and age must be between 18 and 100")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String createdAt;

    private String avatar;

    private MultipartFile image;
}
