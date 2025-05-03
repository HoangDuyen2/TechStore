package hcmute.edu.vn.techstore.controller.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("userController")
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/my-account")
    public String myAccount(Model model) {
        return "web/profile";
    }
}
