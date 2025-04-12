package hcmute.edu.vn.techstore.controller;

import hcmute.edu.vn.techstore.model.response.UserResponse;
import hcmute.edu.vn.techstore.service.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController
{
    @Autowired
    IUserService userService;

    @ModelAttribute("user")
    public UserResponse getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = SecurityUtils.getCurrentUsername();
            return userService.getUserByEmail(email);
        }
        return null;
    }
}