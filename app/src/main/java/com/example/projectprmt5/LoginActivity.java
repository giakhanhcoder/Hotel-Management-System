package com.example.projectprmt5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Màn hình đăng nhập
 * Người dùng: 1 - Authentication
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // UI Components
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView tvForgotPassword;
    private TextView tvRegister;
    private ProgressBar progressBar;

    // Repository
    private UserRepository userRepository;

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "HotelManagerPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_REMEMBER_ME = "rememberMe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_template);

        // Initialize
        initViews();
        initRepository();
        checkRememberMe();
        setupListeners();
    }

    /**
     * Khởi tạo các view
     */
    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        // Create ProgressBar programmatically (add to layout later if needed)
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);

        // SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        // DEBUG: Long click on logo/title to open debug activity
        View rootView = findViewById(android.R.id.content);
        rootView.setOnLongClickListener(v -> {
            startActivity(new Intent(this, DatabaseDebugActivity.class));
            return true;
        });
    }

    /**
     * Khởi tạo repository
     */
    private void initRepository() {
        userRepository = new UserRepository(getApplication());
    }

    /**
     * Kiểm tra xem user đã chọn "Remember Me" trước đó chưa
     */
    private void checkRememberMe() {
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);

        if (isLoggedIn && rememberMe) {
            // Tự động đăng nhập
            String role = sharedPreferences.getString(KEY_USER_ROLE, "");
            navigateToDashboard(role);
            finish();
        }
    }

    /**
     * Setup listeners cho các views
     */
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Xử lý đăng nhập
     */
    private void handleLogin() {
        // Get input
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d(TAG, "========== LOGIN DEBUG START ==========");
        Log.d(TAG, "Email nhập vào: " + email);
        Log.d(TAG, "Password nhập vào: " + password);
        Log.d(TAG, "Password length: " + password.length());

        // Validate
        if (!validateInput(email, password)) {
            Log.e(TAG, "Validation failed!");
            return;
        }

        // Show loading
        showLoading(true);

        // Hash password (simple hash - in production use BCrypt)
        String hashedPassword = "HASH_" + password;
        Log.d(TAG, "Password sau khi hash: " + hashedPassword);

        // Login in background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Log.d(TAG, "Bắt đầu query database...");
                
                // Call repository login
                User user = userRepository.login(email, hashedPassword).get();

                Log.d(TAG, "Kết quả query: " + (user != null ? "Tìm thấy user" : "KHÔNG tìm thấy user"));
                
                if (user != null) {
                    Log.d(TAG, "User ID: " + user.getUserId());
                    Log.d(TAG, "User Email: " + user.getEmail());
                    Log.d(TAG, "User Full Name: " + user.getFullName());
                    Log.d(TAG, "User Role: " + user.getRole());
                    Log.d(TAG, "User isActive: " + user.isActive());
                    Log.d(TAG, "Password trong DB: " + user.getPasswordHash());
                }

                runOnUiThread(() -> {
                    showLoading(false);

                    if (user != null && user.isActive()) {
                        // Login success
                        Log.d(TAG, "✅ LOGIN THÀNH CÔNG!");
                        Log.d(TAG, "========== LOGIN DEBUG END ==========");
                        onLoginSuccess(user);
                    } else if (user != null && !user.isActive()) {
                        // Account is inactive
                        Log.w(TAG, "❌ Tài khoản bị vô hiệu hóa");
                        Log.d(TAG, "========== LOGIN DEBUG END ==========");
                        Toast.makeText(this, "Tài khoản đã bị vô hiệu hóa", 
                                     Toast.LENGTH_SHORT).show();
                    } else {
                        // Login failed
                        Log.e(TAG, "❌ LOGIN THẤT BẠI - Không tìm thấy user hoặc password sai");
                        Log.d(TAG, "========== LOGIN DEBUG END ==========");
                        Toast.makeText(this, getString(R.string.invalid_credentials), 
                                     Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "❌ Exception khi login: " + e.getMessage());
                e.printStackTrace();
                Log.d(TAG, "========== LOGIN DEBUG END ==========");
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), 
                                 Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Validate input
     */
    private boolean validateInput(String email, String password) {
        // Check email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.field_required));
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.invalid_email));
            etEmail.requestFocus();
            return false;
        }

        // Check password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.field_required));
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError(getString(R.string.password_too_short));
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Xử lý khi login thành công
     */
    private void onLoginSuccess(User user) {
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putBoolean(KEY_REMEMBER_ME, cbRememberMe.isChecked());
        editor.apply();

        // Navigate to dashboard theo role
        navigateToDashboard(user.getRole());
        finish();
    }

    /**
     * Navigate đến Dashboard tương ứng với role
     */
    private void navigateToDashboard(String role) {
        Intent intent;

        switch (role) {
            case User.Role.MANAGER:
                // TODO: Create ManagerDashboardActivity
                Toast.makeText(this, "Welcome Manager!", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MainActivity.class);
                break;

            case User.Role.RECEPTIONIST:
                // TODO: Create ReceptionistDashboardActivity
                Toast.makeText(this, "Welcome Receptionist!", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MainActivity.class);
                break;

            case User.Role.GUEST:
                Toast.makeText(this, "Welcome Guest!", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, GuestDashboardActivity.class);
                break;

            default:
                Toast.makeText(this, "Unknown role, defaulting to Main.", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MainActivity.class);
                break;
        }

        startActivity(intent);
    }

    /**
     * Show/hide loading
     */
    private void showLoading(boolean show) {
        if (show) {
            btnLogin.setEnabled(false);
            btnLogin.setText(R.string.loading);
            // progressBar.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setEnabled(true);
            btnLogin.setText(R.string.login);
            // progressBar.setVisibility(View.GONE);
        }
    }
}
