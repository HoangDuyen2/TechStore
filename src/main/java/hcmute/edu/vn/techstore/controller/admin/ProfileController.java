package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.dto.interfaces.ChangePassword;
import hcmute.edu.vn.techstore.dto.interfaces.OnUpdate;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller()
@RequestMapping({"/admin","/staff"})
@RequiredArgsConstructor
public class ProfileController {
    private final IUserService userService;
    private final Validator validator;

    @GetMapping("/profile")
    public String adminProfile(Model model) {
        UserResponse userResponse = (UserResponse) model.getAttribute("user");
        UserRequest userRequest = userService.getUserById(userResponse.getUserId());
        model.addAttribute("adminProfile", userRequest);
        return "admin/profile";
    }

    @PostMapping("/update-profile")
    public String updateAdminProfile(@Valid @ModelAttribute("adminProfile") UserRequest userRequest,
                                     BindingResult bindingResult,
                                     Model model) {
        Class<?>[] groups = new Class[]{OnUpdate.class, Default.class};

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest, groups);

        for (ConstraintViolation<UserRequest> violation : violations) {
            bindingResult.rejectValue(violation.getPropertyPath().toString(), " ",violation.getMessage());
        }
        if (bindingResult.hasErrors()) {
            return "admin/profile";
        }

        try {
            if (userService.updateUser(userRequest)){
                return "redirect:/admin/profile";
            }
        }
        catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("adminProfile", userRequest);
        return "admin/profile";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("adminProfile", userRequest);
        return "admin/forgot-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Validated(ChangePassword.class) @ModelAttribute("adminProfile") UserRequest userRequest,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/forgot-password";
        }

        try {
            if (userService.updatePassword(userRequest)){
                return "redirect:/admin/profile";
            }
        }
        catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "admin/forgot-password";
    }
}

