package hcmute.edu.vn.techstore.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminSaleReportController")
@RequestMapping("/admin/sale-report")
@RequiredArgsConstructor
public class SaleReportController {
    @RequestMapping("")
    public String index() {
        return "admin/report";
    }
}
