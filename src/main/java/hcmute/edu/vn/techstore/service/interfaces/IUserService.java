package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.AdminProfileRequest;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    AdminProfileRequest findByAccount_Email(String accountEmail);

    boolean updateAdmin(AdminProfileRequest adminProfileRequest);

    boolean updateAdmin(AdminProfileRequest adminProfileRequest, MultipartFile file);
}
