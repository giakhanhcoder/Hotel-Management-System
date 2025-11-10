package com.example.projectprmt5.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.LoginActivity;
import com.example.projectprmt5.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnManageRooms;
    private Button btnManageServices;
    private Button btnLogout;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "HotelManagerPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_REMEMBER_ME = "rememberMe";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        btnManageRooms = findViewById(R.id.btn_manage_rooms);
        btnManageServices = findViewById(R.id.btn_manage_services);
        btnLogout = findViewById(R.id.btn_logout);

        btnManageRooms.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminRoomManagementActivity.class);
            startActivity(intent);
        });

        btnManageServices.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminServiceManagementActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            logoutUser();
        });
    }

    private void logoutUser() {
        // Clear SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_IS_LOGGED_IN);
        // Keep KEY_REMEMBER_ME choice, but clear login status
        // If you want to clear everything:
        // editor.clear();
        editor.apply();

        // Navigate back to LoginActivity
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
