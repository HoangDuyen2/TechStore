package hcmute.edu.vn.techstore.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class Test {
    @GetMapping("")
    public String index() {
        return "web/index1";
    }
}
