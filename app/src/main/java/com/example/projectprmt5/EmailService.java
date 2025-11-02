package com.example.projectprmt5;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Service để gửi email OTP
 * Email Service for sending OTP
 */
public class EmailService {

    private static final String TAG = "EmailService";
    
    // Email configuration - Cần cấu hình email của bạn ở đây
    // Email configuration - Configure your email here
    
    // Gmail Configuration
    private static final String EMAIL_FROM = "nguyentrungtien512003@gmail.com";
    private static final String EMAIL_PASSWORD = "tgioxuppdxghzoos"; // App Password từ Google (loại bỏ khoảng trắng)
    
    // Gmail SMTP Settings
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; // Hoặc "465" cho SSL
    
    // Lưu ý: 
    // 1. Phải sử dụng App Password từ Google, không phải mật khẩu thông thường
    // 2. Để tạo App Password: Google Account > Security > 2-Step Verification > App passwords
    // 3. Loại bỏ tất cả khoảng trắng trong App Password
    
    // ExecutorService để chạy task trong background thread
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    // Handler để callback về main thread
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Callback interface
    public interface EmailCallback {
        void onSuccess();
        void onFailure(String error);
    }

    /**
     * Gửi OTP qua email
     * Send OTP via email
     */
    public static void sendOTPEmail(String toEmail, String otp, EmailCallback callback) {
        executorService.execute(() -> {
            String errorMessage = null;
            boolean success = false;

            try {
                // Configure properties
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.ssl.trust", SMTP_HOST);
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
                
                // Gmail specific settings
                if (SMTP_HOST.contains("gmail")) {
                    props.put("mail.smtp.starttls.required", "true");
                    props.put("mail.smtp.ssl.trust", "*");
                    props.put("mail.smtp.connectiontimeout", "10000");
                    props.put("mail.smtp.timeout", "10000");
                }
                
                // FPT email specific settings (if needed)
                if (SMTP_HOST.contains("fpt")) {
                    props.put("mail.smtp.ssl.enable", "true");
                }

                // Create session
                Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                        }
                    });

                // Create message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL_FROM));
                message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));
                message.setSubject("Mã OTP Đặt Lại Mật Khẩu - Password Reset OTP");
                message.setText(buildEmailBody(otp));

                // Send message
                Transport.send(message);

                Log.d(TAG, "✅ Email sent successfully to: " + toEmail);
                success = true;

            } catch (MessagingException e) {
                errorMessage = e.getMessage();
                Log.e(TAG, "❌ Error sending email: " + errorMessage, e);
                
                // Log chi tiết hơn để debug
                if (e.getCause() != null) {
                    Log.e(TAG, "Caused by: " + e.getCause().getMessage());
                }
                e.printStackTrace();
                
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e(TAG, "❌ Unexpected error: " + errorMessage, e);
                e.printStackTrace();
            }

            // Callback về main thread
            final boolean finalSuccess = success;
            final String finalError = errorMessage;
            mainHandler.post(() -> {
                if (callback != null) {
                    if (finalSuccess) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(finalError != null ? finalError : "Unknown error");
                    }
                }
            });
        });
    }

    /**
     * Tạo nội dung email
     */
    private static String buildEmailBody(String otp) {
        return "Xin chào,\n\n" +
               "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.\n\n" +
               "Mã OTP của bạn là: " + otp + "\n\n" +
               "Mã này có hiệu lực trong 5 phút.\n" +
               "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
               "Trân trọng,\n" +
               "Hệ thống Quản lý Khách sạn\n\n" +
               "---\n\n" +
               "Hello,\n\n" +
               "You have requested to reset your password.\n\n" +
               "Your OTP code is: " + otp + "\n\n" +
               "This code is valid for 5 minutes.\n" +
               "If you did not request a password reset, please ignore this email.\n\n" +
               "Best regards,\n" +
               "Hotel Management System";
    }

    /**
     * Kiểm tra xem email service đã được cấu hình chưa
     */
    public static boolean isConfigured() {
        return EMAIL_PASSWORD != null && 
               !EMAIL_PASSWORD.isEmpty() && 
               !EMAIL_PASSWORD.equals("your_app_password_here") &&
               !EMAIL_PASSWORD.equals("your_password_here");
    }

    /**
     * Thiết lập email credentials (có thể lưu vào SharedPreferences hoặc config)
     */
    public static void setEmailCredentials(String email, String password) {
        // TODO: Implement saving credentials securely
        Log.d(TAG, "Email credentials updated");
    }
}

