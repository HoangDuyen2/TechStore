package hcmute.edu.vn.techstore.controller.staff;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("staffHomeController")
@RequestMapping("/staff")
@RequiredArgsConstructor
public class HomeController {
    @GetMapping("/home")
    public String dashboard() {
        return "admin/index";
    }
}
