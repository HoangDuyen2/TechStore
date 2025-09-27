package hcmute.edu.vn.techstore.controller;

import hcmute.edu.vn.techstore.dto.request.ForgotPasswordRequest;
import hcmute.edu.vn.techstore.dto.request.ResetPasswordRequest;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public String postRegisterPage(@Validated @ModelAttribute("userRequest") UserRequest userRequest, BindingResult bindingResult, Model model) throws Exception {
        if (bindingResult.hasErrors()) {
            return "web/create-account";
        }
        try {
            userRequest.setRoleName("ROLE_CUSTOMER");
            if (userService.register(userRequest)) {
                model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
                return "web/create-account";
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
                model.addAttribute("error", "Account is locked/inactive. Please contact administrator/verify your account.");
            } else {
                model.addAttribute("error", "Invalid username or password");
            }
        }
        return "web/login-account";
    }

    @GetMapping("/forgot-password")
    public String getForgotPasswordPage(Model model) {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        model.addAttribute("forgotPasswordRequest", forgotPasswordRequest);
        return "web/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest forgotPasswordRequest,
                                        BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "web/forgot-password";
        }
        try {
            boolean emailSent = userService.forgotPassword(forgotPasswordRequest);
            if (emailSent) {
                model.addAttribute("successMessage", "Email đặt lại mật khẩu đã được gửi! Vui lòng kiểm tra hộp thư của bạn.");
            } else {
                model.addAttribute("errorMessage", "Email không tồn tại trong hệ thống!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "web/forgot-password";
    }

    @GetMapping("/reset-password")
    public String getResetPasswordPage(@RequestParam(value = "token", required = false) String token, Model model) {
        // Kiểm tra token có tồn tại không
        if (token == null || token.isEmpty()) {
            model.addAttribute("errorMessage", "Token không tồn tại! Vui lòng yêu cầu reset mật khẩu mới.");
            return "web/reset-password";
        }
        
        // Kiểm tra token có hợp lệ không
        boolean isValidToken = userService.isValidResetToken(token);
        
        if (!isValidToken) {
            model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn! Vui lòng yêu cầu reset mật khẩu mới.");
            return "web/reset-password";
        }
        
        // Chỉ tạo form khi token hợp lệ
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setToken(token);
        model.addAttribute("resetPasswordRequest", resetPasswordRequest);
        return "web/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest resetPasswordRequest,
                                       BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "web/reset-password";
        }
        try {
            boolean passwordReset = userService.resetPassword(resetPasswordRequest);
            if (passwordReset) {
                model.addAttribute("successMessage", "Mật khẩu đã được đặt lại thành công! Bạn có thể đăng nhập với mật khẩu mới.");
                model.addAttribute("showLoginLink", true);
            } else {
                model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "web/reset-password";
    }

    @GetMapping("/logout")
    public String getLogoutPage(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);
        return "web/index1";
    }
}
