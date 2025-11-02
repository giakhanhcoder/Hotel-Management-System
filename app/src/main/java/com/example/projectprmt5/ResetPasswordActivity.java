package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * Màn hình đặt lại mật khẩu sau khi quên
 * Reset Password Activity (after forgot password)
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";

    // UI Components
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnReset;
    private Button btnCancel;
    private TextView tvEmail;

    // Repository
    private UserRepository userRepository;

    // User email from intent
    private String userEmail;

    // Validation pattern
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Log.d(TAG, "Reset Password screen started");

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize
        initViews();
        initRepository();
        setupListeners();
        displayEmail();
    }

    /**
     * Khởi tạo các views
     */
    private void initViews() {
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnReset = findViewById(R.id.btnReset);
        btnCancel = findViewById(R.id.btnCancel);
        tvEmail = findViewById(R.id.tvEmail);
    }

    /**
     * Khởi tạo repository
     */
    private void initRepository() {
        userRepository = new UserRepository(getApplication());
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
        btnReset.setOnClickListener(v -> handleResetPassword());

        btnCancel.setOnClickListener(v -> {
            finish(); // Quay lại LoginActivity
        });

        // Real-time validation
        setupRealtimeValidation();
    }

    /**
     * Setup real-time validation khi người dùng nhập
     */
    private void setupRealtimeValidation() {
        // New Password validation
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilNewPassword.setError(null);
                tilNewPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Confirm Password validation
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilConfirmPassword.setError(null);
                tilConfirmPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Xử lý reset password
     */
    private void handleResetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate
        if (!validateInput(newPassword, confirmPassword)) {
            return;
        }

        // Show loading
        btnReset.setEnabled(false);
        btnReset.setText(R.string.loading);

        // Reset password in background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Get user by email
                User user = userRepository.getUserByEmail(userEmail).get();

                if (user == null) {
                    runOnUiThread(() -> {
                        btnReset.setEnabled(true);
                        btnReset.setText(getString(R.string.reset_password));
                        Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }

                // Update password
                String newPasswordHash = "HASH_" + newPassword; // Simple hash (in production use BCrypt)
                user.setPasswordHash(newPasswordHash);

                // Save to database
                userRepository.update(user);

                runOnUiThread(() -> {
                    btnReset.setEnabled(true);
                    btnReset.setText(getString(R.string.reset_password));
                    
                    Toast.makeText(this, 
                            getString(R.string.reset_password_success), 
                            Toast.LENGTH_LONG).show();

                    Log.d(TAG, "✅ Password reset successful for: " + userEmail);

                    // Navigate to LoginActivity
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                Log.e(TAG, "❌ Error resetting password: " + e.getMessage());
                e.printStackTrace();

                runOnUiThread(() -> {
                    btnReset.setEnabled(true);
                    btnReset.setText(getString(R.string.reset_password));
                    Toast.makeText(this, 
                            getString(R.string.reset_password_failed) + ": " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Validate input
     */
    private boolean validateInput(String newPassword, String confirmPassword) {
        boolean isValid = true;

        // Clear all previous errors
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validate New Password
        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError(getString(R.string.field_required));
            if (isValid) etNewPassword.requestFocus();
            isValid = false;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError(getString(R.string.password_too_short));
            if (isValid) etNewPassword.requestFocus();
            isValid = false;
        } else if (newPassword.length() > 50) {
            tilNewPassword.setError(getString(R.string.password_too_long));
            if (isValid) etNewPassword.requestFocus();
            isValid = false;
        } else if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            tilNewPassword.setError(getString(R.string.password_weak));
            if (isValid) etNewPassword.requestFocus();
            isValid = false;
        } else if (newPassword.contains(" ")) {
            tilNewPassword.setError(getString(R.string.password_no_spaces));
            if (isValid) etNewPassword.requestFocus();
            isValid = false;
        }

        // Validate Confirm Password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.field_required));
            if (isValid) etConfirmPassword.requestFocus();
            isValid = false;
        } else if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.passwords_not_match));
            if (isValid) etConfirmPassword.requestFocus();
            isValid = false;
        }

        return isValid;
    }
}

