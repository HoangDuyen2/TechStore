package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.convert.UserResponseConverter;
import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.entity.ImageEntity;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.model.request.UserRequest;
import hcmute.edu.vn.techstore.model.response.UserResponse;
import hcmute.edu.vn.techstore.repository.AccountRepository;
import hcmute.edu.vn.techstore.repository.RoleRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.IUserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserResponseConverter userResponseConverter;

    @Autowired
    AccountRepository accountRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public boolean register(UserRequest userRequest) throws IOException {
        if (isExists(userRequest))
            return false;
        if (isValidation(userRequest))
            return false;
        AccountEntity accountEntity = AccountEntity
                    .builder()
                    .email(userRequest.getEmail())
                    .password(bCryptPasswordEncoder.encode(userRequest.getPassword()))
                    .build();

        ImageEntity imageEntity = ImageEntity
                    .builder()
                    .imagePath(addImage(userRequest))
                    .build();
        RoleEntity role = roleRepository.findByName(ERole.valueOf(userRequest.getRoleName()))
                .orElseThrow(() -> new RuntimeException("Role not found"));
        UserEntity user = UserEntity
                    .builder()
                    .account(accountEntity)
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .dateOfBirth(userRequest.getDateOfBirth())
                    .gender(userRequest.getGender())
                    .image(imageEntity)
                    .role(role)
                    .build();
        userRepository.save(user);
        return true;
    }

    public boolean isExists(UserRequest userRequest) {
        if (emailExists(userRequest.getEmail())) {
            throw new BadCredentialsException("Email is exists");
        }
        if (phoneNumberExists(userRequest.getPhoneNumber())) {
            throw new BadCredentialsException("Phone number is exists");
        }
        return false;
    }

    public boolean isValidation(UserRequest userRequest) {
        if (!EmailValidator.getInstance().isValid(userRequest.getEmail())) {
            throw new BadCredentialsException("Email is not valid");
        }
        if (!validateTenDigitsNumber(userRequest.getPhoneNumber()))
        {
            throw new BadCredentialsException("Invalid phone number");
        }
        if (!validatePassword(userRequest.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }
        if (!checkPassword(userRequest.getPassword(), userRequest.getConfirmPassword())) {
            throw new BadCredentialsException("Password not match");
        }
        return false;
    }

    public boolean emailExists(String email) {
        return userRepository.findByAccount_Email(email) != null;
    }

    public boolean phoneNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber) != null;
    }

    public boolean validateTenDigitsNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile(("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"));
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public boolean validatePassword(String password) {
        String regExpn = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.@#$%^&+=])(?=\\S+$).{8,20}$";
        Pattern pattern = Pattern.compile(regExpn);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean checkPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public String addImage(UserRequest userRequest) throws IOException {
        String imagePath = "/uploads/default-image.png";

        MultipartFile imageFile = userRequest.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFilename = imageFile.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalFilename;
            Path uploadPath = Paths.get("src/main/resources/static/uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(imageFile.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            imagePath = "/uploads/" + fileName;
        }
        return imagePath;
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<UserEntity> userEntities = userRepository.findAll(pageable);
        return userEntities.map(userResponseConverter::toUserResponse);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByAccount_Email(email);

        return userResponseConverter.toUserResponse(userEntity);
    }
}
