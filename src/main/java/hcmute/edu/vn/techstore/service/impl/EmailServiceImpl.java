package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.service.interfaces.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String to, String verificationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Xác thực tài khoản TechStore");
        
        String verificationUrl = baseUrl + "/verify-email?token=" + verificationToken;
        String emailContent = String.format("""
            Xin chào!
            
            Cảm ơn bạn đã đăng ký tài khoản tại TechStore.
            
            Vui lòng nhấn vào liên kết sau để xác thực tài khoản của bạn:
            %s
            
            Liên kết này sẽ hết hạn sau 24 giờ.
            
            Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này.
            
            Trân trọng,
            Đội ngũ TechStore
            """, verificationUrl);
        
        message.setText(emailContent);
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Đặt lại mật khẩu TechStore");
        
        String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
        String emailContent = String.format("""
            Xin chào!
            
            Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản TechStore.
            
            Vui lòng nhấn vào liên kết sau để đặt lại mật khẩu:
            %s
            
            Liên kết này sẽ hết hạn sau 1 giờ.
            
            Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
            
            Trân trọng,
            Đội ngũ TechStore
            """, resetUrl);
        
        message.setText(emailContent);
        mailSender.send(message);
    }

    @Override
    public void sendDiscountCode(String to, String code, String value, String unit) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Mã giảm giá từ TechStore");

        String emailContent = String.format("""
            Xin chào!
            
            Chúng tôi rất vui được gửi đến bạn mã giảm giá đặc biệt từ TechStore.
            
            Mã giảm giá của bạn: %s
            
            Chi tiết: Giảm giá %s %s cho đơn hàng tiếp theo của bạn.
            
            Hãy sử dụng mã này trong lần mua sắm tiếp theo để nhận ưu đãi hấp dẫn!
            
            Trân trọng,
            Đội ngũ TechStore
            """, code, value, unit);

        message.setText(emailContent);
        mailSender.send(message);
    }
}