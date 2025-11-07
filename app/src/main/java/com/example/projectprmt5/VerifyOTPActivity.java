package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Màn hình xác nhận OTP để reset password
 * Verify OTP Activity for password reset
 */
public class VerifyOTPActivity extends AppCompatActivity {

    private static final String TAG = "VerifyOTPActivity";

    // UI Components
    private TextInputLayout tilOTP;
    private TextInputEditText etOTP;
    private MaterialButton btnVerify;
    private Button btnResendOTP;
    private TextView tvEmail;
    private TextView tvTimer;
    private TextView tvResendOTP;
    private ProgressBar progressBar;

    // OTP Manager
    private OTPManager otpManager;

    // User email from intent
    private String userEmail;

    // Timer for OTP expiry
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        Log.d(TAG, "Verify OTP screen started");

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize
        initViews();
        initOTPManager();
        setupListeners();
        displayEmail();
        startOTPTimer();
    }

    /**
     * Khởi tạo các views
     */
    private void initViews() {
        tilOTP = findViewById(R.id.tilOTP);
        etOTP = findViewById(R.id.etOTP);
        btnVerify = findViewById(R.id.btnVerify);
        btnResendOTP = findViewById(R.id.btnResendOTP);
        tvEmail = findViewById(R.id.tvEmail);
        tvTimer = findViewById(R.id.tvTimer);
        progressBar = findViewById(R.id.progressBar);
        tvResendOTP = findViewById(R.id.tvResendOTP);
    }

    /**
     * Khởi tạo OTP Manager
     */
    private void initOTPManager() {
        otpManager = new OTPManager(this);
    }

    /**
     * Display email để confirm
     */
    private void displayEmail() {
        if (tvEmail != null && userEmail != null) {
            tvEmail.setText(userEmail);
        }
    }

    /**
     * Setup listeners
     */
    private void setupListeners() {
        btnVerify.setOnClickListener(v -> handleVerifyOTP());

        btnResendOTP.setOnClickListener(v -> handleResendOTP());

        // Back to Forgot Password (if exists in layout)
        View tvBackToForgot = findViewById(R.id.tvBackToForgot);
        if (tvBackToForgot != null) {
            tvBackToForgot.setOnClickListener(v -> {
                finish(); // Quay lại ForgotPasswordActivity
            });
        }

        // Real-time validation
        etOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilOTP.setError(null);
                tilOTP.setErrorEnabled(false);
                
                // Auto format OTP (remove spaces, limit to 6 digits)
                String text = s.toString().replaceAll("[^0-9]", "");
                if (text.length() > 6) {
                    text = text.substring(0, 6);
                }
                
                if (!text.equals(s.toString())) {
                    etOTP.setText(text);
                    etOTP.setSelection(text.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Start OTP countdown timer
     */
    private void startOTPTimer() {
        long remainingSeconds = otpManager.getRemainingTime(userEmail);
        
        if (remainingSeconds <= 0) {
            // OTP expired
            tvTimer.setText("OTP đã hết hạn");
            btnResendOTP.setEnabled(true);
            tvResendOTP.setVisibility(View.VISIBLE);
            return;
        }

        // Start countdown
        countDownTimer = new CountDownTimer(remainingSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                tvTimer.setText(String.format("OTP còn hiệu lực: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("OTP đã hết hạn");
                btnResendOTP.setEnabled(true);
                tvResendOTP.setVisibility(View.VISIBLE);
            }
        };

        countDownTimer.start();
    }

    /**
     * Xử lý verify OTP
     */
    private void handleVerifyOTP() {
        String inputOTP = etOTP.getText().toString().trim();

        // Validate
        if (!validateInput(inputOTP)) {
            return;
        }

        // Show loading
        showLoading(true);

        // Verify OTP in background
        new Thread(() -> {
            try {
                // Simulate network delay (in production, this would be server call)
                Thread.sleep(500);

                boolean isValid = otpManager.verifyOTP(userEmail, inputOTP);

                runOnUiThread(() -> {
                    showLoading(false);

                    if (isValid) {
                        // OTP valid - Navigate to ResetPasswordActivity
                        Toast.makeText(this, 
                                getString(R.string.otp_verified), 
                                Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "✅ OTP verified for: " + userEmail);

                        // Navigate to ResetPasswordActivity
                        Intent intent = new Intent(VerifyOTPActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", userEmail);
                        startActivity(intent);
                        finish();
                    } else {
                        // OTP invalid or expired
                        tilOTP.setError(getString(R.string.otp_invalid));
                        etOTP.requestFocus();
                        etOTP.selectAll();
                        
                        Toast.makeText(this, 
                                getString(R.string.otp_invalid), 
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "❌ Error verifying OTP: " + e.getMessage());
                e.printStackTrace();

                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, 
                            getString(R.string.otp_verify_failed) + ": " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    /**
     * Xử lý resend OTP
     */
    private void handleResendOTP() {
        // Navigate back to ForgotPasswordActivity
        Intent intent = new Intent(VerifyOTPActivity.this, ForgotPasswordActivity.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
        finish();
    }

    /**
     * Validate input
     */
    private boolean validateInput(String otp) {
        tilOTP.setError(null);

        if (TextUtils.isEmpty(otp)) {
            tilOTP.setError(getString(R.string.field_required));
            etOTP.requestFocus();
            return false;
        }

        if (otp.length() != 6) {
            tilOTP.setError("OTP phải có 6 chữ số");
            etOTP.requestFocus();
            return false;
        }

        if (!otp.matches("^[0-9]{6}$")) {
            tilOTP.setError("OTP chỉ được chứa số");
            etOTP.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Show/hide loading
     */
    private void showLoading(boolean show) {
        if (show) {
            btnVerify.setEnabled(false);
            btnVerify.setText(R.string.loading);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnVerify.setEnabled(true);
            btnVerify.setText(R.string.verify_otp);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

