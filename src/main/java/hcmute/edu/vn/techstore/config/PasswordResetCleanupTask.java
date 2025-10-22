package hcmute.edu.vn.techstore.config;

import hcmute.edu.vn.techstore.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetCleanupTask {
    
    private final PasswordResetService passwordResetService;
    
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    public void cleanupExpiredTokens() {
        passwordResetService.cleanupExpiredTokens();
    }
}
