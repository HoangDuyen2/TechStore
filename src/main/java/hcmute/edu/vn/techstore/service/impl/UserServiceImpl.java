package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.EGender;
import hcmute.edu.vn.techstore.dto.request.AdminProfileRequest;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IImageService imageService;

    @Override
    public AdminProfileRequest findByAccount_Email(String accountEmail) {
        UserEntity userEntity = userRepository.findByAccount_Email(accountEmail);
        return AdminProfileRequest.builder()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .phoneNumber(userEntity.getPhoneNumber())
                .dateOfBirth(userEntity.getDateOfBirth())
                .gender(userEntity.getGender().name())
                .address(userEntity.getAddress())
                .image(userEntity.getImage().getImagePath())
                .email(userEntity.getAccount().getEmail())
                .password(userEntity.getAccount().getPassword())
                .confirmPassword(userEntity.getAccount().getPassword())
                .build();
    }

    @Override
    public boolean updateAdmin(AdminProfileRequest adminProfileRequest) {
        try {
            UserEntity user = userRepository.findByAccount_Email(adminProfileRequest.getEmail());
            user.setFirstName(adminProfileRequest.getFirstName());
            user.setLastName(adminProfileRequest.getLastName());
            user.setPhoneNumber(adminProfileRequest.getPhoneNumber());
            user.setDateOfBirth(adminProfileRequest.getDateOfBirth());
            user.setGender(EGender.valueOf(adminProfileRequest.getGender()));
            user.setAddress(adminProfileRequest.getAddress());
            user.getAccount().setEmail(adminProfileRequest.getEmail());
            user.getAccount().setPassword(adminProfileRequest.getPassword());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateAdmin(AdminProfileRequest adminProfileRequest, MultipartFile file) {
        try {
            UserEntity user = userRepository.findByAccount_Email(adminProfileRequest.getEmail());
            user.setFirstName(adminProfileRequest.getFirstName());
            user.setLastName(adminProfileRequest.getLastName());
            user.setPhoneNumber(adminProfileRequest.getPhoneNumber());
            user.setDateOfBirth(adminProfileRequest.getDateOfBirth());
            user.setGender(EGender.valueOf(adminProfileRequest.getGender()));
            user.setAddress(adminProfileRequest.getAddress());
            user.getAccount().setEmail(adminProfileRequest.getEmail());
            user.getAccount().setPassword(adminProfileRequest.getPassword());
            user.getImage().setImagePath(imageService.updateImage(file, user.getImage().getImagePath()));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
