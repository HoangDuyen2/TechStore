package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.AdminProfileRequest;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    AdminProfileRequest findByAccount_Email(String accountEmail);
    boolean updateAdmin(AdminProfileRequest adminProfileRequest);
    boolean updateAdmin(AdminProfileRequest adminProfileRequest, MultipartFile file);
    boolean register(UserRequest user) throws IOException;
    List<UserResponse> getAllUsers();
    UserResponse getUserByEmail(String email);
    UserRequest getUserById(Long id);
    boolean updateUser(UserRequest user) throws IOException;
    boolean updateActived(Long id, boolean actived);
}
