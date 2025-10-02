package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.dto.request.ChangePasswordRequest;
import hcmute.edu.vn.techstore.dto.request.ForgotPasswordRequest;
import hcmute.edu.vn.techstore.dto.request.ProfileRequest;
import hcmute.edu.vn.techstore.dto.request.ResetPasswordRequest;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import hcmute.edu.vn.techstore.dto.response.UserSearchResponse;
import hcmute.edu.vn.techstore.entity.UserEntity;

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
    boolean updateProfile(String email, ProfileRequest profileRequest) throws IOException;
    boolean changePassword(String email, ChangePasswordRequest changePasswordRequest) throws IOException;
    UserEntity findByEmail(String email);
    boolean verifyEmail(String verificationToken);
    boolean forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    boolean resetPassword(ResetPasswordRequest resetPasswordRequest);
    boolean isValidResetToken(String token);
    List<UserSearchResponse> searchUsers(String keyword);

    boolean isValidEmailOrPhoneNumber(String input);
}
