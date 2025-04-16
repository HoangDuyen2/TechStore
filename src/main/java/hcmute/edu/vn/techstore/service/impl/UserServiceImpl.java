package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.EGender;
import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.convert.UserResponseConverter;
import hcmute.edu.vn.techstore.dto.request.AdminProfileRequest;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.exception.DateOfBirthException;
import hcmute.edu.vn.techstore.repository.RoleRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserResponseConverter userResponseConverter;
    private final IImageService imageService;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean register(UserRequest userRequest) throws IOException {
        validateExists(userRequest);

        if (!checkPassword(userRequest.getPassword(), userRequest.getConfirmPassword())) {
            throw new BadCredentialsException("Password not match");
        }

        if (userRequest.getDateOfBirth() != null) {
            validateDateOfBirth(userRequest.getDateOfBirth());
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
                    .address(userRequest.getAddress())
                    .gender(userRequest.getGender())
                    .isActived(true)
                    .role(role)
                    .build();

        if ((user.getImage() != null && !user.getImage().isEmpty())) {
            user.setImage(imageService.saveImage(userRequest.getImage()));
        }
        else user.setImage("default.png");

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

    public void validateExists(UserRequest userRequest) {
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
    }

    public UserEntity emailExists(String email) {
        return userRepository.findByAccount_Email(email).orElse(null);
    }

    public UserEntity phoneNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    public void validateDateOfBirth(LocalDate dateOfBirth) {
        // Kiểm tra nếu ngày sinh trong tương lai
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new DateOfBirthException("Date of birth is not valid");
        }

        // Kiểm tra tuổi: ít nhất 18 tuổi và không quá 100 tuổi
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 18 || age > 100) {
            throw new DateOfBirthException("You must be between 18 and 100 years old");
        }
    }

    public boolean checkPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
    @Override
    public UserResponse getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByAccount_Email(email).orElse(null);
        return userResponseConverter.toUserResponse(userEntity);
    }

    @Override
    public UserRequest getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(null);
        return userResponseConverter.toUserRequest(userEntity);
    }

    @Override
    public boolean updateUser(UserRequest user) throws IOException {
        UserEntity oldEntity = userRepository.findById(user.getUserId()).orElseThrow(null);
        UserEntity newEntity = userResponseConverter.toUserEntity(user);
        if (oldEntity != null) {
            validateExists(user);

            if (user.getDateOfBirth() != null) {
                validateDateOfBirth(user.getDateOfBirth());
            }

            AccountEntity accountEntity = oldEntity.getAccount();
            accountEntity.setEmail(user.getEmail());
            newEntity.setAccount(accountEntity);

            if ((user.getImage() != null && !user.getImage().isEmpty())) {
                newEntity.setImage(imageService.saveImage(user.getImage()));
            } else newEntity.setImage(oldEntity.getImage());
            newEntity.setActived(oldEntity.isActived());
            userRepository.save(newEntity);
            return true;
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
    public List<UserResponse> getAllUsersNotContains(ERole role) {
        List<UserEntity> userEntities = userRepository.findByRoleNotContains(role);
        return userEntities.stream().map(userResponseConverter::toUserResponse).toList();
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

    @Override
    public boolean updateAdmin(AdminProfileRequest adminProfileRequest) {
        try {
            UserEntity user = userRepository.findByAccount_Email(adminProfileRequest.getEmail()).orElse(null);
            user.setFirstName(adminProfileRequest.getFirstName());
            user.setLastName(adminProfileRequest.getLastName());
            user.setPhoneNumber(adminProfileRequest.getPhoneNumber());
            user.setDateOfBirth(adminProfileRequest.getDateOfBirth());
            user.setGender(EGender.valueOf(adminProfileRequest.getGender()));
            user.setAddress(adminProfileRequest.getAddress());
            user.getAccount().setEmail(adminProfileRequest.getEmail());
            user.getAccount().setPassword(adminProfileRequest.getPassword());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateAdmin(AdminProfileRequest adminProfileRequest, MultipartFile file) {
        try {
            UserEntity user = userRepository.findByAccount_Email(adminProfileRequest.getEmail()).orElse(null);
            user.setFirstName(adminProfileRequest.getFirstName());
            user.setLastName(adminProfileRequest.getLastName());
            user.setPhoneNumber(adminProfileRequest.getPhoneNumber());
            user.setDateOfBirth(adminProfileRequest.getDateOfBirth());
            user.setGender(EGender.valueOf(adminProfileRequest.getGender()));
            user.setAddress(adminProfileRequest.getAddress());
            user.getAccount().setEmail(adminProfileRequest.getEmail());
            user.getAccount().setPassword(adminProfileRequest.getPassword());
            user.setImage(imageService.updateImage(file, user.getImage()));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
