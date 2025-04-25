package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;

import java.io.IOException;

public interface IUserFactory {
    UserEntity createUser(UserRequest userRequest, AccountEntity accountEntity, RoleEntity roleEntity) throws IOException;
}
