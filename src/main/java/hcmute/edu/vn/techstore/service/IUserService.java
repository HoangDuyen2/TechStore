package hcmute.edu.vn.techstore.service;

import hcmute.edu.vn.techstore.model.request.UserRequest;
import hcmute.edu.vn.techstore.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    boolean register(UserRequest user) throws IOException;
    List<UserResponse> getAllUsers();
    UserResponse getUserByEmail(String email);
    UserRequest getUserById(Long id);
    boolean updateUser(UserRequest user) throws IOException;
}
