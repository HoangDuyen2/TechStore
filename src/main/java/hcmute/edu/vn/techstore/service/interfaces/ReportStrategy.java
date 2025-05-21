package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.response.ReportResponse;
import java.time.LocalDate;

public interface ReportStrategy {
    ReportResponse generateReport(LocalDate startDate, LocalDate endDate);
} 