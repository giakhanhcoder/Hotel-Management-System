package com.example.projectprmt5;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;

/**
 * AddEditUserActivity
 * Form to add or edit user (both Staff and Customer)
 * Role is pre-selected based on USER_TYPE intent extra
 */
public class AddEditUserActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etPassword;
    private Spinner spinnerRole;
    private Button btnSave;

    private UserRepository userRepository;
    private int userId = -1;
    private String userType;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_user);

        // Get intent data
        userId = getIntent().getIntExtra("USER_ID", -1);
        userType = getIntent().getStringExtra("USER_TYPE");
        isEditMode = userId != -1;

        setupToolbar();
        initViews();
        initRepository();
        setupRoleSpinner();
        setupListeners();

        if (isEditMode) {
            loadUserData();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String title = isEditMode ? "Sửa người dùng" : "Thêm người dùng";
            getSupportActionBar().setTitle(title);
        }
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnSave = findViewById(R.id.btnSave);
    }

    private void initRepository() {
        userRepository = new UserRepository(getApplication());
    }

    private void setupRoleSpinner() {
        String[] roles = {"GUEST", "RECEPTIONIST", "MANAGER"};
        String[] roleLabels = {"Khách hàng", "Lễ tân", "Quản lý"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            roleLabels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        // Pre-select role based on user type
        if (userType != null) {
            if (userType.equals("RECEPTIONIST")) {
                spinnerRole.setSelection(1);
            } else if (userType.equals("GUEST")) {
                spinnerRole.setSelection(0);
            }
        }
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveUser());
    }

    private void loadUserData() {
        userRepository.getUserById(userId).observe(this, user -> {
            if (user != null) {
                etFullName.setText(user.getFullName());
                etEmail.setText(user.getEmail());
                etPhone.setText(user.getPhoneNumber());
                etPassword.setHint("Để trống nếu không đổi mật khẩu");

                // Set role spinner
                String role = user.getRole();
                if ("MANAGER".equals(role)) {
                    spinnerRole.setSelection(2);
                } else if ("RECEPTIONIST".equals(role)) {
                    spinnerRole.setSelection(1);
                } else {
                    spinnerRole.setSelection(0);
                }
            }
        });
    }

    private void saveUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Vui lòng nhập họ tên");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (!isEditMode && TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Get selected role
        String[] roles = {"GUEST", "RECEPTIONIST", "MANAGER"};
        String selectedRole = roles[spinnerRole.getSelectedItemPosition()];

        if (isEditMode) {
            // Update existing user
            userRepository.getUserById(userId).observe(this, user -> {
                if (user != null) {
                    user.setFullName(fullName);
                    user.setEmail(email);
                    user.setPhoneNumber(phone);
                    user.setRole(selectedRole);

                    // Only update password if provided
                    if (!TextUtils.isEmpty(password)) {
                        user.setPasswordHash(password);
                    }

                    userRepository.update(user);
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            // Create new user
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhoneNumber(phone);
            newUser.setPasswordHash(password);
            newUser.setRole(selectedRole);
            newUser.setActive(true);

            userRepository.insert(newUser);
            Toast.makeText(this, "Thêm người dùng thành công", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
