package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.convert.UserResponseConverter;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.service.interfaces.IUserRegistrationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("ROLE_CUSTOMER")
@RequiredArgsConstructor
public class CustomerRegistrationStrategy implements IUserRegistrationStrategy {
    private final UserResponseConverter userResponseConverter;
    @Override
    public UserEntity createUser(UserRequest userRequest) throws IOException {
        UserEntity user = userResponseConverter.toUserEntity(userRequest);
        user.setActived(false);
        return user;
    }
}
