package hcmute.edu.vn.techstore.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class HomeController {


    @RequestMapping("/dashboard")
    public String dashboard() {
        return "admin/index";
    }
}
