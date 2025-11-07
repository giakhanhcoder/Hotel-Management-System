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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Màn hình hồ sơ cá nhân
 * Profile Activity
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // UI Components
    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilAddress;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etAddress;
    private TextView tvRole;
    private TextView tvCreatedAt;
    private TextView tvLastLogin;
    private MaterialButton btnChangePassword;
    private MaterialButton btnSave;
    private Button btnCancel;

    // Repository
    private UserRepository userRepository;

    // Current user
    private User currentUser;

    // Validation patterns
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0|\\+84)(\\s|\\.)?([0-9]{9})$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZàáảãạâầấẩẫậăằắẳẵặèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵđÀÁẢÃẠÂẦẤẨẪẬĂẰẮẲẴẶÈÉẺẼẸÊỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴĐ\\s]+$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d(TAG, "Profile screen started");

        // Initialize
        initViews();
        initRepository();

        // Get user ID from intent or SharedPreferences
        int userId = getIntent().getIntExtra("user_id", -1);

        // TODO: In production, get userId from SharedPreferences or AuthManager
        // For now, hardcode for testing (replace with actual logged-in user)
        if (userId == -1) {
            userId = 1; // Test user
        }

        loadUserProfile(userId);
        setupListeners();
    }

    /**
     * Khởi tạo các views
     */
    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilAddress = findViewById(R.id.tilAddress);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        tvRole = findViewById(R.id.tvRole);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvLastLogin = findViewById(R.id.tvLastLogin);
        btnChangePassword = findViewById(R.id.btnChangePassword);
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
     * Load user profile từ database
     */
    private void loadUserProfile(int userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User user = userRepository.getUserByIdSync(userId).get();

                runOnUiThread(() -> {
                    if (user != null) {
                        currentUser = user;
                        displayUserInfo(user);
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error loading user profile: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Hiển thị thông tin user
     */
    private void displayUserInfo(User user) {
        etFullName.setText(user.getFullName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhoneNumber());
        etAddress.setText(user.getAddress());

        // Role
        String roleText = getRoleDisplayText(user.getRole());
        tvRole.setText("Vai trò: " + roleText);

        // Dates
        if (user.getCreatedAt() != null) {
            tvCreatedAt.setText("Ngày tạo: " + formatDate(user.getCreatedAt()));
        }
        if (user.getLastLoginAt() != null) {
            tvLastLogin.setText("Đăng nhập gần nhất: " + formatDate(user.getLastLoginAt()));
        }
    }

    private String formatDate(Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Get role display text
     */
    private String getRoleDisplayText(String role) {
        if (role == null) return "Khách hàng";
        switch (role) {
            case User.Role.GUEST:
                return getString(R.string.role_guest);
            case User.Role.RECEPTIONIST:
                return getString(R.string.role_receptionist);
            case User.Role.MANAGER:
                return getString(R.string.role_manager);
            default:
                return "Khách hàng";
        }
    }

    /**
     * Setup listeners
     */
    private void setupListeners() {
        btnSave.setOnClickListener(v -> handleSaveProfile());

        btnCancel.setOnClickListener(v -> {
            // Restore original values
            if (currentUser != null) {
                displayUserInfo(currentUser);
            }
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            intent.putExtra("user_id", currentUser.getUserId());
            startActivity(intent);
        });

        // Real-time validation
        setupRealtimeValidation();
    }

    /**
     * Setup real-time validation
     */
    private void setupRealtimeValidation() {
        etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilFullName.setError(null);
                tilFullName.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPhone.setError(null);
                tilPhone.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Xử lý lưu profile
     */
    private void handleSaveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Validate
        if (!validateInput(fullName, email, phone)) {
            return;
        }

        // Update user
        currentUser.setFullName(fullName);
        currentUser.setPhoneNumber(phone);
        currentUser.setAddress(address);
        // Note: Email should not be changed here (requires separate verification)

        // Save to database
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                userRepository.update(currentUser);

                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e(TAG, "Error updating profile: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Validate input
     */
    private boolean validateInput(String fullName, String email, String phone) {
        boolean isValid = true;

        // Clear all errors
        tilFullName.setError(null);
        tilPhone.setError(null);

        // Validate Full Name
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError(getString(R.string.field_required));
            if (isValid) etFullName.requestFocus();
            isValid = false;
        } else if (fullName.trim().length() < 2) {
            tilFullName.setError(getString(R.string.name_too_short));
            if (isValid) etFullName.requestFocus();
            isValid = false;
        } else if (!NAME_PATTERN.matcher(fullName.trim()).matches()) {
            tilFullName.setError(getString(R.string.name_invalid_characters));
            if (isValid) etFullName.requestFocus();
            isValid = false;
        }

        // Validate Phone
        if (!TextUtils.isEmpty(phone)) {
            String cleanPhone = phone.replaceAll("[\\s.]", "");
            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                tilPhone.setError(getString(R.string.phone_invalid_format));
                if (isValid) etPhone.requestFocus();
                isValid = false;
            }
        }

        return isValid;
    }
}