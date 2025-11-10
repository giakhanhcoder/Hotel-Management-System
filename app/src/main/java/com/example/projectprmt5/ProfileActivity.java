package com.example.projectprmt5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.MenuItem;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Màn hình hồ sơ cá nhân
 * Profile Activity
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int REQUEST_CODE_PERMISSION = 1002;

    // UI Components
    private com.google.android.material.appbar.MaterialToolbar toolbar;
    private ImageView imgAvatar;
    private ImageView imgEditAvatar;
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
    private String currentAvatarPath;

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
        setupToolbar();
        initRepository();

        // Get user ID from intent or SharedPreferences
        int userId = getIntent().getIntExtra("user_id", -1);

        // If not in intent, get from SharedPreferences
        if (userId == -1) {
            android.content.SharedPreferences prefs = getSharedPreferences("HotelManagerPrefs", MODE_PRIVATE);
            userId = prefs.getInt("userId", -1);
        }

        // If still not found, show error and finish
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
        toolbar = findViewById(R.id.toolbar);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);
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
     * Setup toolbar với back button
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.profile));
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
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

        // Load avatar
        loadAvatar(user);
    }

    /**
     * Load và hiển thị avatar từ file path
     */
    private void loadAvatar(User user) {
        // Lưu avatar path hiện tại
        currentAvatarPath = user.getIdPhotoPath();
        
        if (currentAvatarPath != null && !currentAvatarPath.isEmpty()) {
            File imageFile = new File(currentAvatarPath);
            if (imageFile.exists()) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    if (bitmap != null) {
                        imgAvatar.setImageBitmap(bitmap);
                        return;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading avatar: " + e.getMessage());
                }
            }
        }
        
        // Nếu không có avatar, hiển thị default icon
        imgAvatar.setImageResource(R.drawable.ic_launcher_foreground);
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

        // Avatar click listeners
        imgAvatar.setOnClickListener(v -> pickImageFromGallery());
        imgEditAvatar.setOnClickListener(v -> pickImageFromGallery());

        // Real-time validation
        setupRealtimeValidation();
    }

    /**
     * Pick image from gallery
     */
    private void pickImageFromGallery() {
        // Check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 
                    REQUEST_CODE_PERMISSION);
                return;
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 
                    REQUEST_CODE_PERMISSION);
                return;
            }
        }

        // Open gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Cần quyền truy cập ảnh để thay đổi avatar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    // Save image to app internal storage
                    String savedPath = saveImageToStorage(selectedImageUri);
                    if (savedPath != null) {
                        // Load and display image
                        loadImageFromPath(savedPath);
                        // Update current avatar path
                        currentAvatarPath = savedPath;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing image: " + e.getMessage());
                    Toast.makeText(this, "Lỗi xử lý ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Save image from URI to app storage
     */
    private String saveImageToStorage(Uri imageUri) {
        try {
            // Create directory for avatars
            File avatarsDir = new File(getFilesDir(), "avatars");
            if (!avatarsDir.exists()) {
                avatarsDir.mkdirs();
            }

            // Create unique filename
            String filename = "avatar_" + currentUser.getUserId() + "_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(avatarsDir, filename);

            // Copy image from URI to file
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                return null;
            }

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Load image from file path and display
     */
    private void loadImageFromPath(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    imgAvatar.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image from path: " + e.getMessage());
        }
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
        
        // Update avatar path if changed
        if (currentAvatarPath != null && !currentAvatarPath.isEmpty()) {
            currentUser.setIdPhotoPath(currentAvatarPath);
        }
        
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