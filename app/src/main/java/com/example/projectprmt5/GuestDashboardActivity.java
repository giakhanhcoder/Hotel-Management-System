package com.example.projectprmt5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.activity.RoomListActivity;

public class GuestDashboardActivity extends AppCompatActivity {

    private Button btnManageBookings, btnLogout, btnAvailableRooms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_dashboard);

        btnManageBookings = findViewById(R.id.btn_manage_bookings);
        btnLogout = findViewById(R.id.btn_logout);
        btnAvailableRooms = findViewById(R.id.btn_available_rooms);

        btnManageBookings.setOnClickListener(v -> {
            startActivity(new Intent(GuestDashboardActivity.this, BookingDashboardActivity.class));
        });

        btnAvailableRooms.setOnClickListener(v -> {
            startActivity(new Intent(GuestDashboardActivity.this, RoomListActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            // Clear SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("HotelManagerPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Navigate to LoginActivity
            Intent intent = new Intent(GuestDashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }
}
