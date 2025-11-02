package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * Màn hình đăng ký
 * Registration Activity
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    // UI Components
    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhoneNumber;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhoneNumber;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private CheckBox cbTerms;
    private Button btnRegister;
    private TextView tvLogin;

    // Repository
    private UserRepository userRepository;

    // Role is always GUEST for public registration
    // Only Manager can create Receptionist/Manager accounts
    private static final String REGISTER_ROLE = User.Role.GUEST;

    // Validation patterns
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0|\\+84)(\\s|\\.)?([0-9]{9})$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZàáảãạâầấẩẫậăằắẳẵặèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵđÀÁẢÃẠÂẦẤẨẪẬĂẰẮẲẴẶÈÉẺẼẸÊỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴĐ\\s]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize
        initViews();
        initRepository();
        setupListeners();
    }

    /**
     * Khởi tạo các view
     */
    private void initViews() {
        // Get TextInputLayouts for error display
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        // Get EditTexts
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    /**
     * Khởi tạo repository
     */
    private void initRepository() {
        userRepository = new UserRepository(getApplication());
    }

    /**
     * Setup listeners cho các views
     */
    private void setupListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());

        tvLogin.setOnClickListener(v -> {
            // Navigate back to Login
            finish();
        });

        // Real-time validation
        setupRealtimeValidation();
    }

    /**
     * Setup real-time validation khi người dùng nhập
     */
    private void setupRealtimeValidation() {
        // Full Name validation
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

        // Email validation
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

        // Phone number validation
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPhoneNumber.setError(null);
                tilPhoneNumber.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Password validation
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
                tilPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Confirm password validation
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
     * Xử lý đăng ký
     */
    private void handleRegister() {
        // Get input
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        Log.d(TAG, "========== REGISTER DEBUG START ==========");
        Log.d(TAG, "Full Name: " + fullName);
        Log.d(TAG, "Email: " + email);
        Log.d(TAG, "Phone: " + phoneNumber);
        Log.d(TAG, "Role: " + REGISTER_ROLE + " (auto-assigned as GUEST)");

        // Validate
        if (!validateInput(fullName, email, phoneNumber, password, confirmPassword)) {
            Log.e(TAG, "Validation failed!");
            return;
        }

        // Show loading
        showLoading(true);

        // Register in background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Log.d(TAG, "Starting registration...");

                // Check if email already exists
                Boolean emailExists = userRepository.checkEmailExists(email).get();

                if (emailExists != null && emailExists) {
                    Log.w(TAG, "Email already exists: " + email);
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(this, getString(R.string.email_already_exists),
                                Toast.LENGTH_SHORT).show();
                        etEmail.setError(getString(R.string.email_already_exists));
                        etEmail.requestFocus();
                    });
                    return;
                }

                // Register new user (always as GUEST)
                // Only Manager can create Receptionist/Manager accounts
                Long userId = userRepository.registerUser(
                        email,
                        password,
                        fullName,
                        REGISTER_ROLE,  // Always GUEST for public registration
                        phoneNumber
                ).get();

                Log.d(TAG, "Registration successful! User ID: " + userId);
                Log.d(TAG, "========== REGISTER DEBUG END ==========");

                runOnUiThread(() -> {
                    showLoading(false);

                    if (userId != null && userId > 0) {
                        // Registration success
                        Toast.makeText(this, getString(R.string.register_success),
                                Toast.LENGTH_SHORT).show();

                        // Navigate back to Login
                        finish();
                    } else {
                        // Registration failed
                        Toast.makeText(this, getString(R.string.register_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "❌ Exception during registration: " + e.getMessage());
                e.printStackTrace();
                Log.d(TAG, "========== REGISTER DEBUG END ==========");

                runOnUiThread(() -> {
                    showLoading(false);

                    String errorMessage;
                    if (e.getMessage() != null && e.getMessage().contains("Email already exists")) {
                        errorMessage = getString(R.string.email_already_exists);
                        etEmail.setError(errorMessage);
                        etEmail.requestFocus();
                    } else {
                        errorMessage = getString(R.string.register_failed) + ": " + e.getMessage();
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Validate input với các quy tắc chặt chẽ
     */
    private boolean validateInput(String fullName, String email, String phoneNumber,
                                  String password, String confirmPassword) {
        boolean isValid = true;

        // Clear all previous errors
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPhoneNumber.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validate Full Name
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError(getString(R.string.field_required));
            if (isValid) etFullName.requestFocus();
            isValid = false;
        } else if (fullName.trim().length() < 2) {
            tilFullName.setError(getString(R.string.name_too_short));
            if (isValid) etFullName.requestFocus();
            isValid = false;
        } else if (fullName.trim().length() > 50) {
            tilFullName.setError(getString(R.string.name_too_long));
            if (isValid) etFullName.requestFocus();
            isValid = false;
        } else if (!NAME_PATTERN.matcher(fullName.trim()).matches()) {
            tilFullName.setError(getString(R.string.name_invalid_characters));
            if (isValid) etFullName.requestFocus();
            isValid = false;
        }

        // Validate Email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.field_required));
            if (isValid) etEmail.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            tilEmail.setError(getString(R.string.invalid_email));
            if (isValid) etEmail.requestFocus();
            isValid = false;
        } else if (email.trim().length() > 100) {
            tilEmail.setError(getString(R.string.email_too_long));
            if (isValid) etEmail.requestFocus();
            isValid = false;
        }

        // Validate Phone Number
        if (TextUtils.isEmpty(phoneNumber)) {
            tilPhoneNumber.setError(getString(R.string.field_required));
            if (isValid) etPhoneNumber.requestFocus();
            isValid = false;
        } else {
            // Remove spaces and dots for validation
            String cleanPhone = phoneNumber.replaceAll("[\\s.]", "");

            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                tilPhoneNumber.setError(getString(R.string.phone_invalid_format));
                if (isValid) etPhoneNumber.requestFocus();
                isValid = false;
            }
        }

        // Validate Password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.field_required));
            if (isValid) etPassword.requestFocus();
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.password_too_short));
            if (isValid) etPassword.requestFocus();
            isValid = false;
        } else if (password.length() > 50) {
            tilPassword.setError(getString(R.string.password_too_long));
            if (isValid) etPassword.requestFocus();
            isValid = false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            tilPassword.setError(getString(R.string.password_weak));
            if (isValid) etPassword.requestFocus();
            isValid = false;
        } else if (password.contains(" ")) {
            tilPassword.setError(getString(R.string.password_no_spaces));
            if (isValid) etPassword.requestFocus();
            isValid = false;
        }

        // Validate Confirm Password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.field_required));
            if (isValid) etConfirmPassword.requestFocus();
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.passwords_not_match));
            if (isValid) etConfirmPassword.requestFocus();
            isValid = false;
        }

        // Validate Terms and Conditions
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, getString(R.string.must_agree_terms),
                    Toast.LENGTH_LONG).show();
            cbTerms.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    /**
     * Show/hide loading
     */
    private void showLoading(boolean show) {
        if (show) {
            btnRegister.setEnabled(false);
            btnRegister.setText(R.string.loading);
        } else {
            btnRegister.setEnabled(true);
            btnRegister.setText(R.string.register);
        }
    }
}