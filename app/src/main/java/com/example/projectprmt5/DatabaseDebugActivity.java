package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.User;

import java.util.List;

/**
 * Activity để debug database
 * Hiển thị tất cả users và thông tin chi tiết
 */
public class DatabaseDebugActivity extends AppCompatActivity {

    private static final String TAG = "DatabaseDebug";
    private TextView tvDebugInfo;
    private Button btnRefresh;
    private Button btnClearDb;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create simple layout programmatically
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        android.widget.LinearLayout linearLayout = new android.widget.LinearLayout(this);
        linearLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        linearLayout.setPadding(32, 32, 32, 32);
        scrollView.addView(linearLayout);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("🔍 DATABASE DEBUG TOOL");
        tvTitle.setTextSize(24);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setPadding(0, 0, 0, 32);
        linearLayout.addView(tvTitle);
        
        btnRefresh = new Button(this);
        btnRefresh.setText("🔄 Refresh Database Info");
        btnRefresh.setOnClickListener(v -> loadDatabaseInfo());
        linearLayout.addView(btnRefresh);
        
        btnClearDb = new Button(this);
        btnClearDb.setText("🗑️ Clear All Tables");
        btnClearDb.setOnClickListener(v -> clearAllTables());
        linearLayout.addView(btnClearDb);

        // Add Booking Management button
        Button btnGoToBooking = new Button(this);
        btnGoToBooking.setText("▶️ Go to Booking Management");
        btnGoToBooking.setOnClickListener(v -> {
            Intent intent = new Intent(DatabaseDebugActivity.this, BookingDashboardActivity.class);
            startActivity(intent);
        });
        linearLayout.addView(btnGoToBooking);
        
        tvDebugInfo = new TextView(this);
        tvDebugInfo.setTextSize(12);
        tvDebugInfo.setTypeface(android.graphics.Typeface.MONOSPACE);
        tvDebugInfo.setPadding(0, 32, 0, 0);
        linearLayout.addView(tvDebugInfo);
        
        setContentView(scrollView);
        
        database = AppDatabase.getInstance(getApplicationContext());
        loadDatabaseInfo();
    }

    private void loadDatabaseInfo() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<User> users = database.userDao().getAllUsersSync();
                List<Booking> bookings = database.bookingDao().getAllBookingsSync();
                
                StringBuilder info = new StringBuilder();
                info.append("📊 DATABASE INFO\n");
                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                info.append("📁 Database: hotel_management_db\n");
                info.append("👥 Total Users: ").append(users.size()).append("\n");
                info.append("🏨 Total Bookings: ").append(bookings.size()).append("\n\n");

                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                info.append("👤 ALL USERS IN DATABASE:\n");
                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                if (users.isEmpty()) {
                    info.append("⚠️ No users found.\n\n");
                } else {
                    for (User user : users) {
                        info.append("ID: ").append(user.getUserId()).append("\n");
                        info.append("Email: ").append(user.getEmail()).append("\n");
                        info.append("Full Name: ").append(user.getFullName()).append("\n");
                        info.append("Role: ").append(user.getRole()).append("\n");
                        info.append("Active: ").append(user.isActive()).append("\n\n");
                    }
                }

                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                info.append("🏨 ALL BOOKINGS IN DATABASE:\n");
                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                if (bookings.isEmpty()) {
                    info.append("⚠️ No bookings found.\n\n");
                } else {
                    for (Booking booking : bookings) {
                        info.append("ID: ").append(booking.getBookingId()).append("\n");
                        info.append("Code: ").append(booking.getBookingCode()).append("\n");
                        info.append("Guest ID: ").append(booking.getGuestId()).append("\n");
                        info.append("Room ID: ").append(booking.getRoomId()).append("\n");
                        info.append("Status: ").append(booking.getStatus()).append("\n");
                        info.append("Check-in: ").append(booking.getCheckInDate()).append("\n");
                        info.append("Check-out: ").append(booking.getCheckOutDate()).append("\n\n");
                    }
                }

                runOnUiThread(() -> {
                    tvDebugInfo.setText(info.toString());
                    Toast.makeText(DatabaseDebugActivity.this, "✅ Loaded " + users.size() + " users & " + bookings.size() + " bookings", Toast.LENGTH_SHORT).show();
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading database info", e);
                runOnUiThread(() -> tvDebugInfo.setText("❌ ERROR:\n" + e.getMessage()));
            }
        });
    }

    private void clearAllTables() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("⚠️ Xác nhận")
            .setMessage("Bạn có chắc muốn XÓA DỮ LIỆU trong tất cả các bảng không?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    database.clearAllTables();
                    runOnUiThread(() -> {
                        Toast.makeText(this, "✅ Đã xóa dữ liệu các bảng.", Toast.LENGTH_SHORT).show();
                        loadDatabaseInfo(); // Refresh the view
                    });
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}
