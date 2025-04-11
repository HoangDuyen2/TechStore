package hcmute.edu.vn.techstore.controller.customer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("userHomeController")
public class HomeController {
    @GetMapping("/home")
    public String home(Model model) {
        return "web/index1";
    }
}
