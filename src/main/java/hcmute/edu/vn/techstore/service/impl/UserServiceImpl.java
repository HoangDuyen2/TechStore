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
import hcmute.edu.vn.techstore.repository.RoleRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import hcmute.edu.vn.techstore.service.interfaces.IUserFactory;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import hcmute.edu.vn.techstore.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserResponseConverter userResponseConverter;
    private final ImageUtil imageUtil;
    private final UserFactoryProducer userFactoryProducer;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean register(UserRequest userRequest) throws IOException {
        validateEmailAndPhoneUniqueness(userRequest);

        if (!isPasswordConfirmed(userRequest.getPassword(), userRequest.getConfirmPassword())) {
            throw new BadCredentialsException("Password not match");
        }

        AccountEntity accountEntity = AccountEntity
                    .builder()
                    .email(userRequest.getEmail())
                    .password(bCryptPasswordEncoder.encode(userRequest.getPassword()))
                    .build();

        RoleEntity role = roleRepository.findByName(ERole.valueOf(userRequest.getRoleName()))
                .orElseThrow(() -> new RuntimeException("Role not found"));

        IUserFactory userFactory = userFactoryProducer.getFactory(userRequest.getRoleName());
        UserEntity user = userFactory.createUser(userRequest,accountEntity, role);

        userRepository.save(user);
        return true;
    }

    public void validateEmailAndPhoneUniqueness(UserRequest userRequest) {
        boolean isNewUser = userRequest.getUserId() == null;

        UserEntity existingUserByEmail = emailExists(userRequest.getEmail());
        if (existingUserByEmail != null &&
                (isNewUser || !userRequest.getUserId().equals(existingUserByEmail.getId()))) {
            throw new BadCredentialsException("Email already exists");
        }

        UserEntity existingUserByPhone = phoneNumberExists(userRequest.getPhoneNumber());
        if (existingUserByPhone != null &&
                (isNewUser || !userRequest.getUserId().equals(existingUserByPhone.getId()))) {
            throw new BadCredentialsException("Phone number already exists");
        }
    }

    public UserEntity emailExists(String email) {
        return userRepository.findByAccount_Email(email).orElse(null);
    }

    public UserEntity phoneNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    public boolean isPasswordConfirmed(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
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
            validateEmailAndPhoneUniqueness(user);

            AccountEntity accountEntity = oldEntity.getAccount();
            accountEntity.setEmail(user.getEmail());
            newEntity.setAccount(accountEntity);

            if ((user.getImage() != null && !user.getImage().isEmpty())) {
                if (!oldEntity.getImage().contains("default-user")) {
                    imageUtil.deleteImage(oldEntity.getImage());
                }
                newEntity.setImage(imageUtil.saveImage(user.getImage())); // ✅ Gọi qua bean
            }
            else newEntity.setImage(oldEntity.getImage());
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

    public boolean updatePassword(UserRequest userRequest) {
        UserEntity userEntity = emailExists(userRequest.getEmail());

        if (userEntity == null) {
            throw new BadCredentialsException("Email does not exist");
        }

        if (!isPasswordConfirmed(userRequest.getPassword(), userRequest.getConfirmPassword())) {
            throw new BadCredentialsException("Password not match");
        }

        userEntity.getAccount().setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        userRepository.save(userEntity);
        return true;
    }
}
