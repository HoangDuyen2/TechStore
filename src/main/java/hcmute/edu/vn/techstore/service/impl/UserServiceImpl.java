package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.convert.UserResponseConverter;
import hcmute.edu.vn.techstore.dto.request.AdminProfileRequest;
import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.exception.DateOfBirthException;
import hcmute.edu.vn.techstore.repository.RoleRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.IUserService;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
    private IImageService imageService;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public boolean register(hcmute.edu.vn.techstore.model.request.UserRequest userRequest) throws IOException {
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
                    .image(imageService.saveImage(userRequest.getImage()))
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

    public void addStaffOrAdmin(UserEntity user, hcmute.edu.vn.techstore.model.request.UserRequest userRequest) {
        user.setAddress(userRequest.getAddress());
        user.setCccd(userRequest.getCccd());
        user.setRelativeName(userRequest.getRelativeName());
        userRepository.save(user);
    }

    public boolean isExists(hcmute.edu.vn.techstore.model.request.UserRequest userRequest) {
        UserEntity user = emailExists(userRequest.getEmail());
        if ((user != null && userRequest.getUserId() == null)||
                (user != null&& !userRequest.getUserId().equals(user.getId()))) {
            throw new BadCredentialsException("Email is exists");
        }
        UserEntity phone = phoneNumberExists(userRequest.getPhoneNumber());
        if ((phone != null && userRequest.getUserId() == null)||
                (phone != null&& !userRequest.getUserId().equals(phone.getId()))) {
            throw new BadCredentialsException("Phone number is exists");
        }
        return false;
    }

    public boolean isValidation(hcmute.edu.vn.techstore.model.request.UserRequest userRequest) {
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

    public UserEntity emailExists(String email) {
        return userRepository.findByAccount_Email(email).orElse(null);
    }

    public UserEntity phoneNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
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
    public List<hcmute.edu.vn.techstore.model.response.UserResponse> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities.stream().map(userResponseConverter::toUserResponse).toList();
    }

    @Override
    public hcmute.edu.vn.techstore.model.response.UserResponse getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByAccount_Email(email).orElse(null);
        return userResponseConverter.toUserResponse(userEntity);
    }

    @Override
    public hcmute.edu.vn.techstore.model.request.UserRequest getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(null);
        return userResponseConverter.toUserRequest(userEntity);
    }

    @Override
    public boolean updateUser(hcmute.edu.vn.techstore.model.request.UserRequest user) throws IOException {
        try {
            UserEntity oldEntity = userRepository.findById(user.getUserId()).orElseThrow(null);
            UserEntity newEntity = userResponseConverter.toUserEntity(user);

            if (oldEntity != null) {
                AccountEntity accountEntity = oldEntity.getAccount();
                accountEntity.setEmail(user.getEmail());
                newEntity.setAccount(accountEntity);

                if ((user.getImage() != null && !user.getImage().isEmpty())) {
                    newEntity.setImage(imageService.saveImage(user.getImage()));
                }
                else newEntity.setImage(oldEntity.getImage());
                newEntity.setActived(oldEntity.isActived());
                userRepository.save(newEntity);
                return true;
            }
            userResponseConverter.toUserEntity(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateActived(Long id, boolean status) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(null);
        if (userEntity != null) {
            userEntity.setActived(status);
            userRepository.save(userEntity);
        }
        return true;
    }

    @Override
    public AdminProfileRequest findByAccount_Email(String accountEmail) {
        UserEntity userEntity = userRepository.findByAccount_Email(accountEmail).orElse(null);
        return AdminProfileRequest.builder()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .phoneNumber(userEntity.getPhoneNumber())
                .dateOfBirth(userEntity.getDateOfBirth())
                .gender(userEntity.getGender().name())
                .address(userEntity.getAddress())
                .image(userEntity.getImage())
                .email(userEntity.getAccount().getEmail())
                .password(userEntity.getAccount().getPassword())
                .confirmPassword(userEntity.getAccount().getPassword())
                .build();
    }


}
