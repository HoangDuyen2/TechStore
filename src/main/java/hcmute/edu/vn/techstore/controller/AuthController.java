package hcmute.edu.vn.techstore.controller;

import hcmute.edu.vn.techstore.dto.interfaces.ChangePassword;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);
        return "web/create-account";
    }

    @PostMapping("/register")
    public String postRegisterPage(@Validated @ModelAttribute("userRequest") UserRequest userRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "web/create-account";
        }
        try {
            userRequest.setRoleName("ROLE_CUSTOMER");
            if (userService.register(userRequest)) {
                return "redirect:/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "web/create-account";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model,
                               @RequestParam(value = "error", required = false) String error,
                               HttpSession session) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);

        if (error != null) {
            String errorMessage = (String) session.getAttribute("LOGIN_ERROR");
            session.removeAttribute("LOGIN_ERROR"); // tránh lặp lại
            if (errorMessage != null && errorMessage.contains("Account is locked")) {
                model.addAttribute("error", "Account is locked. Please contact administrator.");
            } else {
                model.addAttribute("error", "Invalid username or password");
            }
        }

        return "web/login-account";
    }



    @GetMapping("/forgot-password")
    public String getForgotPasswordPage(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("changePasswordRequest", userRequest);
        return "web/change-password";
    }

    @PostMapping("/forgot-password")
    public String postForgotPasswordPage(@Validated(ChangePassword.class) @ModelAttribute("userRequest") UserRequest userRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "web/change-password";
        }

        try {
            if (userService.updatePassword(userRequest)){
                return "redirect:/login";
            }
        }
        catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "web/login-account";
    }

    @GetMapping("/logout")
    public String getLogoutPage(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);
        return "web/index1";
    }
}
