package hcmute.edu.vn.techstore.service.impl.strategy;

import hcmute.edu.vn.techstore.dto.response.ReportResponse;
import hcmute.edu.vn.techstore.service.interfaces.ReportStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportContext {
//    Spring sẽ tự động inject tất cả các bean có kiểu ReportStrategy vào Map này
//    key: là tên bean (ví dụ như "customerAnalysisReport" trong @Component("customerAnalysisReport"))
//    value: là chính instance của bean tương ứng
    private final Map<String, ReportStrategy> strategies;
    private final DefaultReportStrategy defaultStrategy;

    public ReportResponse generateReport(String strategyType, LocalDate startDate, LocalDate endDate) {
        ReportStrategy strategy = strategies.getOrDefault(strategyType, defaultStrategy);
        return strategy.generateReport(startDate, endDate);
    }
} 