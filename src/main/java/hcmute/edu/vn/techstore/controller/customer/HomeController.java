package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.model.response.UserResponse;
import hcmute.edu.vn.techstore.service.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("userHomeController")
public class HomeController {

    @Autowired
    IUserService userService;

    private UserResponse getCurrentUser() {
        String email = SecurityUtils.getCurrentUsername();
        return userService.getUserByEmail(email);
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("user", getCurrentUser());
        return "web/index1";
    }
}
