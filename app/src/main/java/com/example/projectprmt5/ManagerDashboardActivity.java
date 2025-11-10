package com.example.projectprmt5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.BookingRepository;
import com.example.projectprmt5.repository.RoomRepository;
import com.example.projectprmt5.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ManagerDashboardActivity
 * 
 * Màn hình dashboard cho quản lý với các KPI và thống kê:
 * - Welcome message với tên quản lý
 * - Current time
 * - Total Revenue (this month)
 * - Occupancy Rate (%)
 * - Total Bookings
 * - Active Rooms
 * - Quick actions
 */
public class ManagerDashboardActivity extends AppCompatActivity {

    private static final String TAG = "ManagerDashboard";
    private static final String PREF_NAME = "HotelManagerPrefs";
    private static final String KEY_USER_ID = "userId";

    // UI Components
    private TextView tvWelcome;
    private TextView tvCurrentTime;
    private TextView tvTotalRevenue;
    private TextView tvOccupancyRate;
    private TextView tvTotalBookings;
    private TextView tvActiveRooms;

    // Repositories
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;

    // Data
    private int currentUserId;

    // Formatters
    private SimpleDateFormat timeFormatter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        initViews();
        initRepositories();
        initFormatters();
        loadCurrentUser();
        setupToolbar();
        setupListeners();
        loadDashboardData();
        updateCurrentTime();

        // Update time every minute
        startTimeUpdater();
    }

    private void initViews() {
        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Welcome & Time
        tvWelcome = findViewById(R.id.tvWelcome);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);

        // KPI Stats (if they exist in layout)
        try {
            tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        } catch (Exception e) {
            Log.d(TAG, "tvTotalRevenue not found in layout");
        }
        try {
            tvOccupancyRate = findViewById(R.id.tvOccupancyRate);
        } catch (Exception e) {
            Log.d(TAG, "tvOccupancyRate not found in layout");
        }
        try {
            tvTotalBookings = findViewById(R.id.tvTotalBookings);
        } catch (Exception e) {
            Log.d(TAG, "tvTotalBookings not found in layout");
        }
        try {
            tvActiveRooms = findViewById(R.id.tvActiveRooms);
        } catch (Exception e) {
            Log.d(TAG, "tvActiveRooms not found in layout");
        }
    }

    private void initRepositories() {
        userRepository = new UserRepository(getApplication());
        bookingRepository = new BookingRepository(getApplication());
        roomRepository = new RoomRepository(getApplication());
    }

    private void initFormatters() {
        timeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setTitle("Dashboard Quản lý");
            }
        }
    }

    private void loadCurrentUser() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = prefs.getInt(KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }

        // Load user info
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User user = userRepository.getUserByIdSync(currentUserId).get();
                if (user != null) {
                    runOnUiThread(() -> {
                        String welcomeText = "Chào mừng, " + (user.getFullName() != null ? user.getFullName() : "Quản lý") + "!";
                        tvWelcome.setText(welcomeText);
                        
                        // Update toolbar title
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Dashboard Quản lý");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading user", e);
            }
        });
    }

    private void setupListeners() {
        // User Management Card
        findViewById(R.id.cardUserManagement).setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });

        // Reports Card
        findViewById(R.id.cardReports).setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, ReportsOverviewActivity.class);
            startActivity(intent);
        });

        // Feedback Card
        findViewById(R.id.cardFeedback).setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, FeedbackListActivity.class);
            startActivity(intent);
        });
    }

    private void loadDashboardData() {
        if (currentUserId == -1) return;

        // Load all bookings for revenue calculation
        bookingRepository.getAllBookings().observe(this, bookings -> {
            if (bookings != null) {
                updateRevenueStats(bookings);
            }
        });

        // Load all rooms for occupancy calculation
        roomRepository.getAllActiveRooms().observe(this, rooms -> {
            if (rooms != null) {
                updateRoomStats(rooms);
            }
        });
    }

    private void updateRevenueStats(List<Booking> bookings) {
        if (bookings == null) return;

        // Calculate total revenue for this month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date monthStart = calendar.getTime();

        double totalRevenue = 0;
        int totalBookings = 0;

        for (Booking booking : bookings) {
            // totalAmount is a primitive double; just check value
            if (booking.getTotalAmount() > 0) {
                // Check if booking is in this month by bookingDate
                Date bookingDate = booking.getBookingDate();
                if (bookingDate != null && bookingDate.after(monthStart)) {
                    totalRevenue += booking.getTotalAmount();
                }
                totalBookings++;
            }
        }

        if (tvTotalRevenue != null) {
            tvTotalRevenue.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", totalRevenue));
        }
        if (tvTotalBookings != null) {
            tvTotalBookings.setText(String.valueOf(totalBookings));
        }
    }

    private void updateRoomStats(List<Room> rooms) {
        if (rooms == null) return;

        int totalRooms = rooms.size();
        int occupiedRooms = 0;

        for (Room room : rooms) {
            if (Room.RoomStatus.OCCUPIED.equals(room.getStatus())) {
                occupiedRooms++;
            }
        }

        double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0;

        if (tvOccupancyRate != null) {
            tvOccupancyRate.setText(String.format(Locale.getDefault(), "%.1f%%", occupancyRate));
        }
        if (tvActiveRooms != null) {
            tvActiveRooms.setText(String.valueOf(totalRooms));
        }
    }

    private void updateCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        String timeText = timeFormatter.format(calendar.getTime());
        tvCurrentTime.setText(timeText);
    }

    private void startTimeUpdater() {
        // Update time every 60 seconds
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000); // 60 seconds
                    runOnUiThread(this::updateCurrentTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(ManagerDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            Toast.makeText(this, "Đang đăng xuất...", Toast.LENGTH_SHORT).show();
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Time updater thread will be stopped when activity is destroyed
    }
}
