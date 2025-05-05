package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.dto.request.AdminProfileRequest;
import hcmute.edu.vn.techstore.dto.request.ProfileRequest;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    boolean register(UserRequest user) throws IOException;
    UserResponse getUserByEmail(String email);
    UserRequest getUserById(Long id);
    boolean updateUser(UserRequest user) throws IOException;
    boolean updateActived(Long id, boolean actived);
    List<UserResponse> getAllUsersNotContains(ERole role);
    boolean updatePassword(UserRequest userRequest);

    ProfileRequest getProfileById(String email);

    boolean updateProfile(String email, ProfileRequest profileRequest);
}
