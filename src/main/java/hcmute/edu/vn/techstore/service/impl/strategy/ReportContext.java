package hcmute.edu.vn.techstore.service.impl.strategy;

import hcmute.edu.vn.techstore.dto.response.ReportResponse;
import hcmute.edu.vn.techstore.service.impl.AccountServiceImpl;
import hcmute.edu.vn.techstore.service.interfaces.ReportStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportContext {
    private final Map<String, ReportStrategy> strategies;
    private final AccountServiceImpl.DefaultReportStrategy defaultStrategy;

    public ReportResponse generateReport(String strategyType, LocalDate startDate, LocalDate endDate) {
        ReportStrategy strategy = strategies.getOrDefault(strategyType, defaultStrategy);
        return strategy.generateReport(startDate, endDate);
    }
} 