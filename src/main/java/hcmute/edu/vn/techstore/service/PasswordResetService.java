package hcmute.edu.vn.techstore.service;

import hcmute.edu.vn.techstore.entity.PasswordResetTokenEntity;
import hcmute.edu.vn.techstore.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {
    
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    
    @Value("${app.password-reset.max-attempts-per-hour:3}")
    private int maxAttemptsPerHour;
    
    @Value("${app.password-reset.token-expiry-hours:1}")
    private int tokenExpiryHours;
    
    @Value("${app.base-url:https://localhost:8443}")
    private String baseUrl;
    
    public boolean sendPasswordResetEmail(String email) {
        // Kiểm tra giới hạn gửi email trong 1 giờ
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        int attemptsInLastHour = tokenRepository.countByEmailAndCreatedAtAfter(email, oneHourAgo);
        
        if (attemptsInLastHour >= maxAttemptsPerHour) {
            return false; // Đã vượt quá giới hạn
        }
        
        // Tạo token mới
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(tokenExpiryHours);
        
        // Lưu token vào database
        PasswordResetTokenEntity tokenEntity = PasswordResetTokenEntity.builder()
                .token(token)
                .email(email)
                .expiryDate(expiryDate)
                .used(false)
                .attempts(0)
                .build();
        
        tokenRepository.save(tokenEntity);
        
        // Gửi email
        sendResetEmail(email, token);
        
        return true;
    }
    
    private void sendResetEmail(String email, String token) {
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("TechStore - Reset Password");
        message.setText(
            "Xin chào,\n\n" +
            "Bạn đã yêu cầu reset mật khẩu cho tài khoản TechStore.\n\n" +
            "Vui lòng click vào link sau để reset mật khẩu:\n" +
            resetUrl + "\n\n" +
            "Link này sẽ hết hạn sau " + tokenExpiryHours + " giờ.\n\n" +
            "Nếu bạn không yêu cầu reset mật khẩu, vui lòng bỏ qua email này.\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ TechStore"
        );
        
        mailSender.send(message);
    }
    
    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .map(tokenEntity -> !tokenEntity.isUsed() && 
                     tokenEntity.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
    
    public String getEmailByToken(String token) {
        return tokenRepository.findByToken(token)
                .map(PasswordResetTokenEntity::getEmail)
                .orElse(null);
    }
    
    public void markTokenAsUsed(String token) {
        tokenRepository.markTokenAsUsed(token);
    }
    
    public void incrementAttempts(String token) {
        tokenRepository.incrementAttempts(token);
    }
    
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
