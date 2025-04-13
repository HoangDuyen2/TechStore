package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.AdminProfileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    AdminProfileRequest findByAccount_Email(String accountEmail);

    boolean updateAdmin(AdminProfileRequest adminProfileRequest);

    boolean updateAdmin(AdminProfileRequest adminProfileRequest, MultipartFile file);

    boolean register(hcmute.edu.vn.techstore.model.request.UserRequest user) throws IOException;
    List<hcmute.edu.vn.techstore.model.response.UserResponse> getAllUsers();
    hcmute.edu.vn.techstore.model.response.UserResponse getUserByEmail(String email);
    hcmute.edu.vn.techstore.model.request.UserRequest getUserById(Long id);
    boolean updateUser(hcmute.edu.vn.techstore.model.request.UserRequest user) throws IOException;
    boolean updateActived(Long id, boolean actived);
}
