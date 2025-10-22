package hcmute.edu.vn.techstore.controller;

import hcmute.edu.vn.techstore.dto.interfaces.OnCreate;
import hcmute.edu.vn.techstore.dto.request.ForgotPasswordEmailRequest;
import hcmute.edu.vn.techstore.dto.request.ResetPasswordRequest;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.repository.AccountRepository;
import hcmute.edu.vn.techstore.service.PasswordResetService;
import hcmute.edu.vn.techstore.service.RateLimitService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final IUserService userService;
    private final PasswordResetService passwordResetService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RateLimitService rateLimitService;

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);
        return "web/create-account";
    }

    @PostMapping("/register")
    public String postRegisterPage(@Validated(OnCreate.class) @ModelAttribute("userRequest") UserRequest userRequest, BindingResult bindingResult, Model model) {
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
        ForgotPasswordEmailRequest request = new ForgotPasswordEmailRequest();
        model.addAttribute("forgotPasswordRequest", request);
        return "web/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String postForgotPasswordPage(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordEmailRequest request, 
                                        BindingResult bindingResult, Model model, HttpServletRequest httpRequest) {
        if (bindingResult.hasErrors()) {
            return "web/forgot-password";
        }

        // Lấy IP address của client
        String clientIp = getClientIpAddress(httpRequest);
        
        // Kiểm tra rate limiting
        if (!rateLimitService.isAllowed(clientIp)) {
            // Vẫn hiển thị thông báo thành công để tránh tiết lộ rate limiting
            model.addAttribute("message", "Nếu email tồn tại trong hệ thống, bạn sẽ nhận được email reset password trong vài phút tới.");
            return "web/forgot-password-success";
        }

        try {
            // BẢO MẬT: Luôn hiển thị thông báo thành công để tránh email enumeration
            // Không tiết lộ email có tồn tại hay không
            boolean emailExists = accountRepository.findByEmail(request.getEmail()).isPresent();
            
            if (emailExists) {
                // Gửi email reset password chỉ khi email tồn tại
                passwordResetService.sendPasswordResetEmail(request.getEmail());
            }
            
            // Luôn hiển thị thông báo thành công để tránh email enumeration
            model.addAttribute("message", "Nếu email tồn tại trong hệ thống, bạn sẽ nhận được email reset password trong vài phút tới.");
            return "web/forgot-password-success";
            
        } catch (Exception e) {
            // Vẫn hiển thị thông báo thành công để không tiết lộ lỗi hệ thống
            model.addAttribute("message", "Nếu email tồn tại trong hệ thống, bạn sẽ nhận được email reset password trong vài phút tới.");
            return "web/forgot-password-success";
        }
    }
    
    /**
     * Lấy IP address thực của client, xử lý proxy và load balancer
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    @GetMapping("/reset-password")
    public String getResetPasswordPage(@RequestParam("token") String token, Model model) {
        if (!passwordResetService.validateToken(token)) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn");
            return "web/reset-password-error";
        }

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        model.addAttribute("resetPasswordRequest", request);
        return "web/reset-password";
    }

    @PostMapping("/reset-password")
    public String postResetPasswordPage(@Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest request, 
                                     BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "web/reset-password";
        }

        try {
            // Kiểm tra token
            if (!passwordResetService.validateToken(request.getToken())) {
                model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn");
                return "web/reset-password-error";
            }

            // Kiểm tra password và confirm password
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                model.addAttribute("error", "Mật khẩu và xác nhận mật khẩu không khớp");
                return "web/reset-password";
            }

            // Lấy email từ token
            String email = passwordResetService.getEmailByToken(request.getToken());
            if (email == null) {
                model.addAttribute("error", "Token không hợp lệ");
                return "web/reset-password-error";
            }

            // Cập nhật mật khẩu
            var account = accountRepository.findByEmail(email).orElse(null);
            if (account != null) {
                account.setPassword(passwordEncoder.encode(request.getPassword()));
                accountRepository.save(account);
                
                // Đánh dấu token đã sử dụng
                passwordResetService.markTokenAsUsed(request.getToken());
                
                model.addAttribute("message", "Mật khẩu đã được reset thành công. Vui lòng đăng nhập lại.");
                return "web/reset-password-success";
            } else {
                model.addAttribute("error", "Không tìm thấy tài khoản");
                return "web/reset-password-error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau.");
            return "web/reset-password";
        }
    }

    @GetMapping("/logout")
    public String getLogoutPage(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);
        return "web/index1";
    }
}
