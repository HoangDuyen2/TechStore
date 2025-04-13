package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.exception.DataNotFoundException;
import hcmute.edu.vn.techstore.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserResponseConverter {
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    public UserResponse toUserResponse (UserEntity item) {
        UserResponse userResponse = modelMapper.map(item, UserResponse.class);
        userResponse.setUserId(item.getId());
        userResponse.setAddress(item.getAddress());
        userResponse.setFullName(item.getLastName() + " " + item.getFirstName());

        if (item.getAccount() != null) {
            userResponse.setEmail(item.getAccount().getEmail());
        }

        if (item.getRole() != null) {
            userResponse.setRoleName(item.getRole().getName().toString());
        }

        if (item.getImage() != null) {
            userResponse.setImage(item.getImage());
        }

        return userResponse;
    }

    public UserRequest toUserRequest (UserEntity userEntity) {
        UserRequest userRequest = modelMapper.map(userEntity, UserRequest.class);
        userRequest.setUserId(userEntity.getId());
        userRequest.setPassword(userEntity.getAccount().getPassword());
        userRequest.setConfirmPassword(userEntity.getAccount().getPassword());
        if (userEntity.getAccount() != null) {
            userRequest.setEmail(userEntity.getAccount().getEmail());
        }

        if (userEntity.getRole() != null) {
            userRequest.setRoleName(userEntity.getRole().getName().toString());
        }

        return userRequest;
    }

    public UserEntity toUserEntity (UserRequest userRequest) {
        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);
        userEntity.setId(userRequest.getUserId());
        RoleEntity role = roleRepository.findByName(ERole.valueOf(userRequest.getRoleName())).orElse(null);
        userEntity.setRole(role);
        return userEntity;
    }
}
