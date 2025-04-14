package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.Enum.EGender;
import hcmute.edu.vn.techstore.dto.request.AdminProfileRequest;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class HomeController {
    private final IUserService userService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/index";
    }

    @GetMapping("/profile")
    public String adminProfile(Model model) {
        AdminProfileRequest adminProfileRequest = userService.findByAccount_Email(SecurityUtils.getCurrentUsername());
        model.addAttribute("adminProfile", adminProfileRequest);
        model.addAttribute("genders", EGender.values());
        return "admin/profile";
    }

    @PostMapping("/update-profile")
    public String updateAdminProfile(@RequestParam("file") MultipartFile file,
                                     @Valid @ModelAttribute("adminProfile") AdminProfileRequest adminProfile,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Validation failed. Please correct the errors and try again.");
            model.addAttribute("genders", EGender.values());
            return "admin/profile";
        }
        if (!Objects.equals(adminProfile.getPassword(), adminProfile.getConfirmPassword())) {
            model.addAttribute("error", "Password and Confirm Password must be the same");
            model.addAttribute("genders", EGender.values());
            return "admin/profile";
        }

        if (file == null || file.isEmpty()) {
            if (userService.updateAdmin(adminProfile)) {
                return "redirect:/admin/profile";
            }
        } else if (!file.isEmpty()) {
            if (userService.updateAdmin(adminProfile, file)) {
                return "redirect:/admin/profile";
            }
        } else {
            model.addAttribute("error", "Something went wrong");
            model.addAttribute("adminProfile", adminProfile);
            model.addAttribute("genders", EGender.values());
        }
        return "admin/profile";
    }
}
