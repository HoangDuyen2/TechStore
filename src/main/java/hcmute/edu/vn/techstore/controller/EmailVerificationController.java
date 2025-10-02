package hcmute.edu.vn.techstore.controller;

import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EmailVerificationController {
    
    private final IUserService userService;

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        try {
            boolean isVerified = userService.verifyEmail(token);
            if (isVerified) {
                model.addAttribute("message", "Tài khoản của bạn đã được xác thực thành công!");
                model.addAttribute("success", true);
            } else {
                model.addAttribute("message", "Liên kết xác thực không hợp lệ hoặc đã hết hạn!");
                model.addAttribute("success", false);
            }
        } catch (Exception e) {
            model.addAttribute("message", "Có lỗi xảy ra khi xác thực tài khoản: " + e.getMessage());
            model.addAttribute("success", false);
        }
        return "web/email-verification";
    }
}