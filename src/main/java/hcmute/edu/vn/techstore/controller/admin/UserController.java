package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.model.response.UserResponse;
import hcmute.edu.vn.techstore.service.IAccountService;
import hcmute.edu.vn.techstore.service.IRoleService;
import hcmute.edu.vn.techstore.service.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("userAdminController")
@RequestMapping("/admin/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IAccountService accountService;

    private UserResponse getCurrentUser() {
        String email = SecurityUtils.getCurrentUsername();
        return userService.getUserByEmail(email);
    }

    @GetMapping("")
    public String userList(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        model.addAttribute("user", getCurrentUser());
        model.addAttribute("users", userService.getAllUsers(PageRequest.of(page, size)));

        return "admin/user/all-user";
    }

    @GetMapping("/component")
    public String component(Model model) {
        model.addAttribute("user", getCurrentUser());
        return "admin/components";
    }
}
