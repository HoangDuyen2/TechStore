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
import java.util.Arrays;
import java.util.List;

@Controller("adminSaleReportController")
@RequestMapping("/admin/sale-report")
@RequiredArgsConstructor
public class SaleReportController {
    private final IOrderService orderService;

    @RequestMapping("")
    public String index(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "default") String reportType,
            Model model) {

        // If no dates provided, default to current month
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        ReportResponse report = orderService.getReport(startDate, endDate, reportType);

        // Add available report types
        List<String> availableReportTypes = Arrays.asList(
            "default",
            "categoryReport",
            "dailySalesReport",
            "customerAnalysisReport"
        );

        model.addAttribute("report", report);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("reportType", reportType);
        model.addAttribute("availableReportTypes", availableReportTypes);

        return "admin/report";
    }
}
