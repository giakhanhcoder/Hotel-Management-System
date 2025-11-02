package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Màn hình quên mật khẩu
 * Forgot Password Activity
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";

    // UI Components
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private Button btnSendReset;
    private TextView tvBackToLogin;
    private ProgressBar progressBar;

    // Repository
    private UserRepository userRepository;

    // OTP Manager
    private OTPManager otpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Log.d(TAG, "Forgot Password screen started");

        // Initialize
        initViews();
        initRepository();
        initOTPManager();
        setupListeners();

        // Check if email is passed from VerifyOTPActivity (resend OTP)
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            etEmail.setText(email);
        }
    }

    /**
     * Khởi tạo các views
     */
    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Khởi tạo repository
     */
    private void initRepository() {
        userRepository = new UserRepository(getApplication());
    }

    /**
     * Khởi tạo OTP Manager
     */
    private void initOTPManager() {
        otpManager = new OTPManager(this);
    }

    /**
     * Setup listeners
     */
    private void setupListeners() {
        btnSendReset.setOnClickListener(v -> handleSendReset());

        tvBackToLogin.setOnClickListener(v -> {
            finish(); // Quay lại LoginActivity
        });

        // Real-time validation
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
                tilEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Xử lý gửi link reset password
     */
    private void handleSendReset() {
        String email = etEmail.getText().toString().trim();

        Log.d(TAG, "========== FORGOT PASSWORD DEBUG START ==========");
        Log.d(TAG, "Email: " + email);

        // Validate
        if (!validateInput(email)) {
            Log.e(TAG, "Validation failed!");
            return;
        }

        // Show loading
        showLoading(true);

        // Check email exists in background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Boolean emailExists = userRepository.checkEmailExists(email).get();

                Log.d(TAG, "Email exists: " + (emailExists != null && emailExists));

                runOnUiThread(() -> {
                    showLoading(false);
                    
                    if (emailExists != null && emailExists) {
                        // Email exists - Generate and send OTP
                        String otp = otpManager.generateOTP();
                        otpManager.saveOTP(email, otp);

                        Log.d(TAG, "✅ Email found, OTP generated: " + otp + " for: " + email);

                        // Gửi OTP qua email
                        // Send OTP via email
                        if (EmailService.isConfigured()) {
                            showLoading(true);
                            btnSendReset.setText("Đang gửi email...");
                            
                            EmailService.sendOTPEmail(email, otp, new EmailService.EmailCallback() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        showLoading(false);
                                        Toast.makeText(ForgotPasswordActivity.this,
                                                getString(R.string.otp_sent) + " " + email,
                                                Toast.LENGTH_LONG).show();

                                        Log.d(TAG, "✅ OTP email sent successfully to: " + email);

                                        // Navigate to VerifyOTPActivity
                                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOTPActivity.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                        finish();
                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    runOnUiThread(() -> {
                                        showLoading(false);
                                        
                                        Log.e(TAG, "❌ Failed to send email: " + error);
                                        
                                        // Fallback: Show OTP in dialog if email fails
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ForgotPasswordActivity.this);
                                        builder.setTitle("OTP Code (Email không thể gửi)");
                                        builder.setMessage("Không thể gửi email. Vui lòng sử dụng mã OTP này:\n\n" + 
                                                         otp + "\n\n(Email error: " + error + ")");
                                        builder.setPositiveButton("OK", null);
                                        builder.show();

                                        Toast.makeText(ForgotPasswordActivity.this,
                                                "Không thể gửi email. Vui lòng xem mã OTP trong dialog.",
                                                Toast.LENGTH_LONG).show();

                                        // Still navigate to VerifyOTPActivity
                                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOTPActivity.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                        finish();
                                    });
                                }
                            });
                        } else {
                            // Email service not configured - show OTP in dialog
                            showLoading(false);
                            
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                            builder.setTitle("OTP Code (Email chưa được cấu hình)");
                            builder.setMessage("Email service chưa được cấu hình. Vui lòng sử dụng mã OTP này:\n\n" + 
                                             otp + "\n\n(Vui lòng cấu hình email trong EmailService.java)");
                            builder.setPositiveButton("OK", null);
                            builder.show();

                            Toast.makeText(this,
                                    "Mã OTP: " + otp + " (Vui lòng cấu hình email service)",
                                    Toast.LENGTH_LONG).show();

                            // Navigate to VerifyOTPActivity
                            Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOTPActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Email not found
                        tilEmail.setError("Email không tồn tại trong hệ thống");
                        etEmail.requestFocus();
                        Toast.makeText(this,
                                "Email không tồn tại trong hệ thống",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "❌ Exception: " + e.getMessage());
                e.printStackTrace();

                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this,
                            "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Validate email input
     */
    private boolean validateInput(String email) {
        // Clear previous error
        tilEmail.setError(null);

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.field_required));
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            tilEmail.setError(getString(R.string.invalid_email));
            etEmail.requestFocus();
            return false;
        }

        if (email.trim().length() > 100) {
            tilEmail.setError(getString(R.string.email_too_long));
            etEmail.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Show/hide loading
     */
    private void showLoading(boolean show) {
        if (show) {
            btnSendReset.setEnabled(false);
            btnSendReset.setText(R.string.loading);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnSendReset.setEnabled(true);
            btnSendReset.setText("Gửi link đặt lại");
            progressBar.setVisibility(View.GONE);
        }
    }
}