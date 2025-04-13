package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("userHomeController")
public class HomeController {

    @Autowired
    IUserService userService;

    @GetMapping("/home")
    public String home(Model model) {
        return "web/index1";
    }
}
