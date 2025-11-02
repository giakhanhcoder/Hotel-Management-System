package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
 * Màn hình đổi mật khẩu
 * Change Password Activity
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";

    // UI Components
    private TextInputLayout tilOldPassword;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etOldPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnSave;
    private Button btnCancel;

    // Repository
    private UserRepository userRepository;

    // Current user
    private User currentUser;

    // Validation pattern
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Log.d(TAG, "Change Password screen started");

        // Initialize
        initViews();
        initRepository();

        // Get user ID from intent
        int userId = getIntent().getIntExtra("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserProfile(userId);
        setupListeners();
    }

    /**
     * Khởi tạo các views
     */
    private void initViews() {
        tilOldPassword = findViewById(R.id.tilOldPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    /**
     * Khởi tạo repository
     */
    private void initRepository() {
        userRepository = new UserRepository(getApplication());
    }

    /**
     * Load user profile để lấy password hash
     */
    private void loadUserProfile(int userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User user = userRepository.getUserByIdSync(userId).get();

                runOnUiThread(() -> {
                    if (user != null) {
                        currentUser = user;
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error loading user: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Setup listeners
     */
    private void setupListeners() {
        btnSave.setOnClickListener(v -> handleChangePassword());

        btnCancel.setOnClickListener(v -> {
            finish(); // Close activity without saving
        });

        // Real-time validation
        setupRealtimeValidation();
    }

    /**
     * Setup real-time validation
     */
    private void setupRealtimeValidation() {
        etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilOldPassword.setError(null);
                tilOldPassword.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

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
     * Xử lý đổi mật khẩu
     */
    private void handleChangePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate
        if (!validateInput(oldPassword, newPassword, confirmPassword)) {
            return;
        }

        // Verify old password
        String oldPasswordHash = "HASH_" + oldPassword; // Simple hash (in production use BCrypt)
        if (!currentUser.getPasswordHash().equals(oldPasswordHash)) {
            tilOldPassword.setError("Mật khẩu cũ không đúng");
            etOldPassword.requestFocus();
            return;
        }

        // Update password
        String newPasswordHash = "HASH_" + newPassword;
        currentUser.setPasswordHash(newPasswordHash);

        // Save to database
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                userRepository.update(currentUser);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                });

            } catch (Exception e) {
                Log.e(TAG, "Error changing password: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Validate input
     */
    private boolean validateInput(String oldPassword, String newPassword, String confirmPassword) {
        boolean isValid = true;

        // Clear all errors
        tilOldPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validate Old Password
        if (TextUtils.isEmpty(oldPassword)) {
            tilOldPassword.setError(getString(R.string.field_required));
            if (isValid) etOldPassword.requestFocus();
            isValid = false;
        }

        // Validate New Password
        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError(getString(R.string.field_required));
            if (isValid) etNewPassword.requestFocus();
            isValid = false;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError(getString(R.string.password_too_short));
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