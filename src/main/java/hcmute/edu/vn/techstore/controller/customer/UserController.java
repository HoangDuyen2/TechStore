package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.dto.request.ChangePasswordRequest;
import hcmute.edu.vn.techstore.dto.request.ProfileRequest;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller("userController")
@RequiredArgsConstructor
public class UserController {
    private final IOrderService orderService;
    private final IUserService userService;

    @GetMapping("/my-account")
    public String myAccount(Model model) {
        model.addAttribute("profileRequest", userService.getProfileById(SecurityUtils.getCurrentUsername()));
        return "web/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(Model model, @Validated @ModelAttribute("profileRequest") ProfileRequest profileRequest,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "web/profile";
        }
        try {
            if (userService.updateProfile(SecurityUtils.getCurrentUsername(), profileRequest)) {
                return "redirect:/my-account";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("profileRequest", userService.getProfileById(SecurityUtils.getCurrentUsername()));
        return "web/profile";
    }

    @GetMapping("/order-history")
    public String manageOrder(Model model) {
        List<OrderResponse> orderResponseList = orderService.getAllOrdersByUserEmail(SecurityUtils.getCurrentUsername());
        model.addAttribute("orderResponseList", orderResponseList);
        return "web/order-history";
    }

    @GetMapping("/change-password")
    public String changePassword(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "web/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(Model model, @Validated @ModelAttribute("changePasswordRequest") ChangePasswordRequest changePasswordRequest,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "web/change-password";
        }
        try {
            userService.changePassword(changePasswordRequest);
            model.addAttribute("message", "Change password successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "web/change-password";
    }
}
