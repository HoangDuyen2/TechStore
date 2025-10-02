package hcmute.edu.vn.techstore.service.interfaces;

public interface IEmailService {
    void sendVerificationEmail(String to, String verificationToken);
    void sendPasswordResetEmail(String to, String resetToken);
}