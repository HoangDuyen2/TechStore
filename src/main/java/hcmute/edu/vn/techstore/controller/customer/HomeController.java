package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("userHomeController")
@RequiredArgsConstructor
public class HomeController {
    private final IUserService userService;

    @GetMapping("/home")
    public String home(Model model) {
        return "web/index1";
    }

    @GetMapping("/products")
    public String getAllProducts() {
        return "web/collection";
    }
}
