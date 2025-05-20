package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.convert.UserResponseConverter;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.service.interfaces.IUserRegistrationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("ROLE_STAFF")
@RequiredArgsConstructor
public class StaffRegistrationStrategy implements IUserRegistrationStrategy {
    private final UserResponseConverter userResponseConverter;
    @Override
    public UserEntity createUser(UserRequest userRequest) throws IOException {
        UserEntity user = userResponseConverter.toUserEntity(userRequest);
        user.setActived(true);

        user.setRelativePhoneNumber(userRequest.getRelativePhoneNumber());
        user.setCccd(userRequest.getCccd());
        user.setRelativeName(userRequest.getRelativeName());
        return user;
    }
}
