package hcmute.edu.vn.techstore.service;

import hcmute.edu.vn.techstore.model.request.UserRequest;
import hcmute.edu.vn.techstore.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
//    boolean addUser(UserRequest user);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserByEmail(String email);
}
