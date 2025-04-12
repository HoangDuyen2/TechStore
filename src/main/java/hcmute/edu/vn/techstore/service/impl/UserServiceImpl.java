package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.convert.UserResponseConverter;
import hcmute.edu.vn.techstore.entity.*;
import hcmute.edu.vn.techstore.exception.DateOfBirthException;
import hcmute.edu.vn.techstore.model.request.UserRequest;
import hcmute.edu.vn.techstore.model.response.UserResponse;
import hcmute.edu.vn.techstore.repository.AccountRepository;
import hcmute.edu.vn.techstore.repository.ImageRepository;
import hcmute.edu.vn.techstore.repository.RoleRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.IImageService;
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
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    @Autowired
    private IImageService imageService;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public boolean register(UserRequest userRequest) throws IOException {
        if (isExists(userRequest))
            return false;
        if (isValidation(userRequest))
            return false;
        if (userRequest.getDateOfBirth() != null) {
            if (!validateDateOfBirth(userRequest.getDateOfBirth()))
                return false;
        }

        AccountEntity accountEntity = AccountEntity
                    .builder()
                    .email(userRequest.getEmail())
                    .password(bCryptPasswordEncoder.encode(userRequest.getPassword()))
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
                    .image(imageService.addImage(userRequest.getImage()))
                    .isActived(true)
                    .role(role)
                    .build();

        if (userRequest.getRoleName().contains("CUSTOMER")){
            addUser(user);
        }
        else addStaffOrAdmin(user, userRequest);
        return true;
    }

    public void addUser(UserEntity user) {
        userRepository.save(user);
    }

    public void addStaffOrAdmin(UserEntity user, UserRequest userRequest) {
        user.setAddress(userRequest.getAddress());
        user.setCccd(userRequest.getCccd());
        user.setRelativeName(userRequest.getRelativeName());
        userRepository.save(user);
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
        if (!userRequest.getRelativePhoneNumber().equals("")){
            if (!validateTenDigitsNumber(userRequest.getRelativePhoneNumber())){
                throw new BadCredentialsException("Invalid relative phone number");
            }
        }
        if (!userRequest.getCccd().equals("")){
            if (!validateTwelveDigitsNumber(userRequest.getCccd())){
                throw new BadCredentialsException("Invalid cccd number");
            }
        }
        return false;
    }

    public boolean emailExists(String email) {
        return userRepository.findByAccount_Email(email) != null;
    }

    public boolean phoneNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber) != null;
    }

    public boolean validateTwelveDigitsNumber(String cccd) {
        // Regex kiểm tra chuỗi gồm đúng 12 chữ số
        Pattern pattern = Pattern.compile("^\\d{12}$");
        Matcher matcher = pattern.matcher(cccd);
        return matcher.matches();
    }


    public boolean validateTenDigitsNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile(("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"));
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public boolean validateDateOfBirth(LocalDate dateOfBirth) {
        // Kiểm tra nếu ngày sinh trong tương lai
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new DateOfBirthException("Date of birth is not valid");
        }

        // Kiểm tra tuổi: ít nhất 18 tuổi và không quá 100 tuổi
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 18 || age > 100) {
            throw new DateOfBirthException("You must be between 18 and 100 years old");
        }

        return true; // Hợp lệ
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
