package vn.clickwork.service.impl;

import java.util.HashMap;
import java.util.Map;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.clickwork.model.OTPCode;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.OTPRequest;
import vn.clickwork.util.OTPUtil;

@Service
public class EmailService {

    private final JavaMailSender sender;
    private final Map<String, OTPCode> otpMap = new HashMap<>();
    private static final Long EXPIRE_TIME = 5 * 60 * 1000L; // 5 minutes

    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    public void sendOTPEmail(String to) {
        String otp = OTPUtil.generateOTP();
        long currentTime = System.currentTimeMillis();
        OTPCode otpCode = new OTPCode(otp, currentTime, 5);
        otpMap.put(to, otpCode);

        // Build HTML content
        String htmlContent = buildHtmlContent(otp);

        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Your OTP Code");
            helper.setText(htmlContent, true); // true indicates HTML

            sender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML OTP email", e);
        }
    }

    private String buildHtmlContent(String otp) {
        return "<!DOCTYPE html>" +
               "<html lang=\"en\">" +
               "<head>" +
               "  <meta charset=\"UTF-8\"/>" +
               "  <style>" +
               "    .container { font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4; }" +
               "    .header { background-color: #2196F3; color: white; padding: 10px; text-align: center; border-radius: 4px 4px 0 0; }" +
               "    .content { background-color: white; padding: 20px; border: 1px solid #ddd; border-top: none; }" +
               "    .otp { display: block; font-size: 24px; font-weight: bold; margin: 20px 0; color: #333; }" +
               "    .footer { font-size: 12px; color: #777; margin-top: 30px; text-align: center; }" +
               "  </style>" +
               "</head>" +
               "<body>" +
               "  <div class=\"container\">" +
               "    <div class=\"header\">" +
               "      <h2>ClickWork</h2>" +
               "    </div>" +
               "    <div class=\"content\">" +
               "      <p>Dear User,</p>" +
               "      <p>Your OTP code is:</p>" +
               "      <span class=\"otp\">" + otp + "</span>" +
               "      <p>This code will expire in <b>1 minutes</b>.</p>" +
               "      <p>If you did not request this, please ignore this email.</p>" +
               "    </div>" +
               "    <div class=\"footer\">" +
               "      &copy; 2025 ClickWork. All rights reserved." +
               "    </div>" +
               "  </div>" +
               "</body>" +
               "</html>";
    }

    public ResponseEntity<Response> verifyOTP(OTPRequest otpModel) {
        OTPCode entry = otpMap.get(otpModel.getEmail());
        if (entry == null) {
            return ResponseEntity.status(401).body(new Response(false, "OTP không hợp lệ", null));
        }

        long now = System.currentTimeMillis();
        if ((now - entry.getCreatedTime()) > EXPIRE_TIME) {
            otpMap.remove(otpModel.getEmail());
            return ResponseEntity.status(401).body(new Response(false, "OTP đã hết hạn", null));
        }

        if (!entry.getCode().equals(otpModel.getInputOtp())) {
            entry.setMaxAttempts(entry.getMaxAttempts() - 1);
            if (entry.getMaxAttempts() <= 0) {
                otpMap.remove(otpModel.getEmail());
                return ResponseEntity.status(401).body(new Response(false, "Đã quá số lần thử OTP", null));
            }
            return ResponseEntity.status(401).body(new Response(false, "OTP không chính xác", null));
        }

        otpMap.remove(otpModel.getEmail());
        return ResponseEntity.ok(new Response(true, "Xác thực thành công", null));
    }
}