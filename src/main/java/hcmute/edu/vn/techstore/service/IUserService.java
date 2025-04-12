package hcmute.edu.vn.techstore.service;

import hcmute.edu.vn.techstore.model.request.UserRequest;
import hcmute.edu.vn.techstore.model.response.UserResponse;

import java.util.List;

public interface IUserService {
//    boolean addUser(UserRequest user);
    List<UserResponse> getAllUsers();
    UserResponse getUserByEmail(String email);
}
