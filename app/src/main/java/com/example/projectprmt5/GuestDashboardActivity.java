package com.example.projectprmt5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GuestDashboardActivity extends AppCompatActivity {

    private Button btnManageBookings;
    private Button btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_dashboard);

        setTitle("Guest Dashboard");

        btnManageBookings = findViewById(R.id.btn_manage_bookings);
        btnLogout = findViewById(R.id.btn_logout);

        btnManageBookings.setOnClickListener(v -> {
            Intent intent = new Intent(GuestDashboardActivity.this, BookingDashboardActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("HotelManagerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate back to LoginActivity
        Intent intent = new Intent(GuestDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
