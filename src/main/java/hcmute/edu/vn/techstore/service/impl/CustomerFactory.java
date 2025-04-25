package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.convert.UserResponseConverter;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.RoleRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import hcmute.edu.vn.techstore.service.interfaces.IUserFactory;
import hcmute.edu.vn.techstore.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Component("customerFactory")
@RequiredArgsConstructor
public class CustomerFactory implements IUserFactory {
    private final UserResponseConverter userResponseConverter;
    private final ImageUtil imageUtil;

    @Override
    public UserEntity createUser(UserRequest userRequest, AccountEntity accountEntity, RoleEntity roleEntity) throws IOException {
        UserEntity user = userResponseConverter.toUserEntity(userRequest);
        user.setAccount(accountEntity);
        user.setActived(true);

        if ((userRequest.getImage() != null && !userRequest.getImage().isEmpty())) {
            if (imageUtil.isValidSuffixImage(Objects.requireNonNull(userRequest.getImage().getOriginalFilename()))) {
                user.setImage(imageUtil.saveImage(userRequest.getImage())); // ✅ Gọi qua bean
            } else {
                throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, PNG, GIF, BMP are allowed.");
            }        }
        else user.setImage("https://res.cloudinary.com/techstore2025/image/upload/v1745396615/default-user_g7vsyp.jpg");
        return user;
    }
}
