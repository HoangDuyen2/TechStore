package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.service.interfaces.IUserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Component("staffFactory")
@RequiredArgsConstructor
public class StaffFactory implements IUserFactory {
    @Qualifier("customerFactory")
    private final IUserFactory customerFactory;
    @Override
    public UserEntity createUser(UserRequest userRequest, AccountEntity accountEntity, RoleEntity roleEntity) throws IOException {
        UserEntity userEntity = customerFactory.createUser(userRequest, accountEntity, roleEntity);
        userEntity.setAddress(userRequest.getAddress());
        userEntity.setCccd(userRequest.getCccd());
        userEntity.setRelativeName(userRequest.getRelativeName());
        return userEntity;
    }
}
