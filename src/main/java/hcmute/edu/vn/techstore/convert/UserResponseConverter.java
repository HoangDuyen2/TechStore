package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.exception.DataNotFoundException;
import hcmute.edu.vn.techstore.model.response.UserResponse;
import hcmute.edu.vn.techstore.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserResponseConverter {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    public UserResponse toUserResponse (UserEntity item) {
        UserResponse userResponse = modelMapper.map(item, UserResponse.class);
        userResponse.setUserId(item.getId());
        userResponse.setAddress(item.getAddress().stream().findFirst().toString());
        userResponse.setFullName(item.getLastName() + " " + item.getFirstName());

        if (item.getAccount() != null) {
            userResponse.setEmail(item.getAccount().getEmail());
        }

        if (item.getRole() != null) {
            userResponse.setRoleName(item.getRole().getName().toString());
        }

        if (item.getImage() != null) {
            userResponse.setImage(item.getImage().getImagePath());
        }

        return userResponse;
    }
}
