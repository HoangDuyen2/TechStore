package hcmute.edu.vn.techstore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class RateLimitService {
    
    // Map để lưu trữ số lần request từ mỗi IP
    private final ConcurrentMap<String, RateLimitInfo> requestCounts = new ConcurrentHashMap<>();
    
    // Cấu hình rate limiting
    private static final int MAX_REQUESTS_PER_HOUR = 5; // Tối đa 5 requests mỗi giờ
    private static final int MAX_REQUESTS_PER_DAY = 20; // Tối đa 20 requests mỗi ngày
    
    public boolean isAllowed(String clientIp) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        LocalDateTime oneDayAgo = now.minusDays(1);
        
        RateLimitInfo info = requestCounts.computeIfAbsent(clientIp, k -> new RateLimitInfo());
        
        // Cleanup old requests
        info.cleanupOldRequests(oneHourAgo, oneDayAgo);
        
        // Kiểm tra giới hạn theo giờ
        if (info.getHourlyRequests() >= MAX_REQUESTS_PER_HOUR) {
            return false;
        }
        
        // Kiểm tra giới hạn theo ngày
        if (info.getDailyRequests() >= MAX_REQUESTS_PER_DAY) {
            return false;
        }
        
        // Thêm request mới
        info.addRequest(now);
        
        return true;
    }
    
    public void recordFailedAttempt(String clientIp) {
        RateLimitInfo info = requestCounts.get(clientIp);
        if (info != null) {
            info.incrementFailedAttempts();
        }
    }
    
    private static class RateLimitInfo {
        private final ConcurrentMap<LocalDateTime, Integer> hourlyRequests = new ConcurrentHashMap<>();
        private final ConcurrentMap<LocalDateTime, Integer> dailyRequests = new ConcurrentHashMap<>();
        private int failedAttempts = 0;
        
        public void addRequest(LocalDateTime timestamp) {
            hourlyRequests.put(timestamp, hourlyRequests.getOrDefault(timestamp, 0) + 1);
            dailyRequests.put(timestamp, dailyRequests.getOrDefault(timestamp, 0) + 1);
        }
        
        public void cleanupOldRequests(LocalDateTime oneHourAgo, LocalDateTime oneDayAgo) {
            hourlyRequests.entrySet().removeIf(entry -> entry.getKey().isBefore(oneHourAgo));
            dailyRequests.entrySet().removeIf(entry -> entry.getKey().isBefore(oneDayAgo));
        }
        
        public int getHourlyRequests() {
            return hourlyRequests.values().stream().mapToInt(Integer::intValue).sum();
        }
        
        public int getDailyRequests() {
            return dailyRequests.values().stream().mapToInt(Integer::intValue).sum();
        }
        
        public void incrementFailedAttempts() {
            failedAttempts++;
        }
        
        public int getFailedAttempts() {
            return failedAttempts;
        }
    }
}
