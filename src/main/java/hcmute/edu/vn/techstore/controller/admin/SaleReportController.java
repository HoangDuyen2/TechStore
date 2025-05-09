package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.dto.response.ReportResponse;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller("adminSaleReportController")
@RequestMapping("/admin/sale-report")
@RequiredArgsConstructor
public class SaleReportController {
    private final IOrderService orderService;

    @RequestMapping("")
    public String index(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        // If no dates provided, default to current month
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        ReportResponse report = orderService.getReport(startDate, endDate);

        model.addAttribute("report", report);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/report";
    }
}
