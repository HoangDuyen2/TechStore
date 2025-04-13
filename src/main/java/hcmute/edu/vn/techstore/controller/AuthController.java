package hcmute.edu.vn.techstore.controller;

import hcmute.edu.vn.techstore.model.request.UserRequest;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AuthController {

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);
        return "web/create-account";
    }

    @PostMapping("/register")
    public String postRegisterPage(@Valid @ModelAttribute("userRequest") UserRequest userRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "web/create-account";
        }
        try {
            if (userRequest.getPassword() == null||userRequest.getPassword().equals("")) {
                model.addAttribute("error","Please enter a password");
            }
            if (userRequest.getConfirmPassword() == null||userRequest.getConfirmPassword().equals("")) {
                model.addAttribute("error","Please enter confirm password");
            }
            userRequest.setRoleName("ROLE_CUSTOMER");
            if (userService.register(userRequest)) {
                System.out.println("Registration successful for user: " + userRequest.getEmail());
                return "redirect:/login";
            }
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "web/create-account";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, @RequestParam(value = "error", required = false) String error) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
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
