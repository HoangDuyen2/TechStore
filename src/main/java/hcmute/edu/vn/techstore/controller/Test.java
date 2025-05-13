package hcmute.edu.vn.techstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class Test {
    @GetMapping("/error")
    public String index() {
        return "web/404";
    }

    @GetMapping("/about-us")
    public String about() {
        return "web/about-us-2";
    }
}
