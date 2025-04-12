package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.model.response.UserResponse;
import hcmute.edu.vn.techstore.service.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class HomeController {

    @Autowired
    IUserService userService;

    private UserResponse getCurrentUser() {
        String email = SecurityUtils.getCurrentUsername();
        return userService.getUserByEmail(email);
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("user", getCurrentUser());
        return "admin/index";
    }
}
