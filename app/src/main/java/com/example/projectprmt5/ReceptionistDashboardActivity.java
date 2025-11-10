package com.example.projectprmt5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.BookingRepository;
import com.example.projectprmt5.repository.RoomRepository;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ReceptionistDashboardActivity
 * 
 * Màn hình dashboard cho lễ tân với ít nhất 8 data items:
 * 1. Welcome message với tên lễ tân
 * 2. Current time
 * 3. Check-ins today count
 * 4. Check-outs today count
 * 5. Occupied rooms count
 * 6. Available rooms count
 * 7. Pending tasks list (RecyclerView với các booking)
 * 8. Pending tasks count
 * 
 * Ngoài ra còn có Quick Actions cho navigation
 */
public class ReceptionistDashboardActivity extends AppCompatActivity {

    private static final String TAG = "ReceptionistDashboard";
    private static final String PREF_NAME = "HotelManagerPrefs";
    private static final String KEY_USER_ID = "userId";

    // UI Components
    private TextView tvWelcome;
    private TextView tvCurrentTime;
    private TextView tvCheckInsToday;
    private TextView tvCheckOutsToday;
    private TextView tvOccupiedRooms;
    private TextView tvAvailableRooms;
    private TextView tvPendingTasksCount;
    private RecyclerView recyclerPendingTasks;
    
    // Quick Actions
    private MaterialCardView cardActionCheckIn;
    private MaterialCardView cardActionCheckOut;
    private MaterialCardView cardActionAllBookings;
    private MaterialCardView cardActionInventory;

    // Repositories
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;

    // Data
    private int currentUserId;
    private List<Booking> allBookings;
    private List<Room> allRooms;
    private BookingAdapter bookingAdapter;

    // Formatters
    private SimpleDateFormat timeFormatter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptionist_dashboard);

        initViews();
        initRepositories();
        initFormatters();
        loadCurrentUser();
        setupToolbar();
        setupRecyclerViews();
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

        // Today's Summary
        tvCheckInsToday = findViewById(R.id.tvCheckInsToday);
        tvCheckOutsToday = findViewById(R.id.tvCheckOutsToday);
        tvOccupiedRooms = findViewById(R.id.tvOccupiedRooms);
        tvAvailableRooms = findViewById(R.id.tvAvailableRooms);

        // Pending Tasks
        tvPendingTasksCount = findViewById(R.id.tvPendingTasksCount);
        recyclerPendingTasks = findViewById(R.id.recyclerPendingTasks);

        // Quick Actions
        cardActionCheckIn = findViewById(R.id.cardActionCheckIn);
        cardActionCheckOut = findViewById(R.id.cardActionCheckOut);
        cardActionAllBookings = findViewById(R.id.cardActionAllBookings);
        cardActionInventory = findViewById(R.id.cardActionInventory);
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
                getSupportActionBar().setTitle("Dashboard Lễ tân");
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
                        String welcomeText = "Chào mừng, " + (user.getFullName() != null ? user.getFullName() : "Lễ tân") + "!";
                        tvWelcome.setText(welcomeText);
                        
                        // Update toolbar title
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Dashboard Lễ tân");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading user", e);
            }
        });
    }

    private void setupRecyclerViews() {
        // Pending Tasks - Vertical
        recyclerPendingTasks.setLayoutManager(new LinearLayoutManager(this));
        bookingAdapter = new BookingAdapter(new ArrayList<>(), booking -> {
            // Navigate to booking detail or check-in/out
            // TODO: Navigate to appropriate activity when created
            Toast.makeText(this, "Booking: " + booking.getBookingCode(), Toast.LENGTH_SHORT).show();
        });
        recyclerPendingTasks.setAdapter(bookingAdapter);
    }

    private void setupListeners() {
        // Quick Actions
        cardActionCheckIn.setOnClickListener(v -> {
            // Navigate to ManualCheckInActivity for walk-in guests
            Intent intent = new Intent(ReceptionistDashboardActivity.this, ManualCheckInActivity.class);
            startActivity(intent);
        });

        cardActionCheckOut.setOnClickListener(v -> {
            // Navigate to ManualCheckOutActivity
            Intent intent = new Intent(ReceptionistDashboardActivity.this, ManualCheckOutActivity.class);
            startActivity(intent);
        });

        cardActionAllBookings.setOnClickListener(v -> {
            Intent intent = new Intent(ReceptionistDashboardActivity.this, BookingDashboardActivity.class);
            startActivity(intent);
        });

        cardActionInventory.setOnClickListener(v -> {
            // TODO: Navigate to InventoryUsageLogActivity when created
            Toast.makeText(this, "Chức năng Inventory đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDashboardData() {
        if (currentUserId == -1) return;

        // Load all bookings
        bookingRepository.getAllBookings().observe(this, bookings -> {
            allBookings = bookings != null ? bookings : new ArrayList<>();
            updateTodayStats();
            updatePendingTasks();
        });

        // Load all rooms
        roomRepository.getAllRooms().observe(this, rooms -> {
            allRooms = rooms != null ? rooms : new ArrayList<>();
            updateRoomStats();
        });
    }

    private void updateTodayStats() {
        if (allBookings == null) return;

        Date now = new Date();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date todayStart = today.getTime();

        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        today.set(Calendar.MILLISECOND, 999);
        Date todayEnd = today.getTime();

        int checkInsToday = 0;
        int checkOutsToday = 0;

        for (Booking booking : allBookings) {
            if (booking.getCheckInDate() != null) {
                // Check if check-in is today
                if (booking.getCheckInDate().after(todayStart) && 
                    booking.getCheckInDate().before(todayEnd)) {
                    checkInsToday++;
                }
            }

            if (booking.getCheckOutDate() != null) {
                // Check if check-out is today
                if (booking.getCheckOutDate().after(todayStart) && 
                    booking.getCheckOutDate().before(todayEnd)) {
                    checkOutsToday++;
                }
            }
        }

        tvCheckInsToday.setText(String.valueOf(checkInsToday));
        tvCheckOutsToday.setText(String.valueOf(checkOutsToday));
    }

    private void updateRoomStats() {
        if (allRooms == null) return;

        int occupiedCount = 0;
        int availableCount = 0;

        for (Room room : allRooms) {
            if (Room.RoomStatus.OCCUPIED.equals(room.getStatus())) {
                occupiedCount++;
            } else if (Room.RoomStatus.AVAILABLE.equals(room.getStatus())) {
                availableCount++;
            }
        }

        tvOccupiedRooms.setText(String.valueOf(occupiedCount));
        tvAvailableRooms.setText(String.valueOf(availableCount));
    }

    private void updatePendingTasks() {
        if (allBookings == null) return;

        Date now = new Date();
        List<Booking> pendingTasks = new ArrayList<>();

        for (Booking booking : allBookings) {
            // Add bookings that need attention:
            // 1. Pending bookings
            // 2. Confirmed bookings with check-in today
            // 3. Checked-in bookings with check-out today

            if (Booking.BookingStatus.PENDING.equals(booking.getStatus())) {
                pendingTasks.add(booking);
            } else if (Booking.BookingStatus.CONFIRMED.equals(booking.getStatus()) &&
                       booking.getCheckInDate() != null &&
                       booking.getCheckInDate().before(now)) {
                // Check-in due today or overdue
                Calendar checkIn = Calendar.getInstance();
                checkIn.setTime(booking.getCheckInDate());
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                
                if (checkIn.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    checkIn.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    pendingTasks.add(booking);
                }
            } else if (Booking.BookingStatus.CHECKED_IN.equals(booking.getStatus()) &&
                       booking.getCheckOutDate() != null) {
                // Check-out due today
                Calendar checkOut = Calendar.getInstance();
                checkOut.setTime(booking.getCheckOutDate());
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                
                if (checkOut.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    checkOut.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    pendingTasks.add(booking);
                }
            }
        }

        // Sort by priority (check-out first, then check-in, then pending)
        pendingTasks.sort((b1, b2) -> {
            int priority1 = getBookingPriority(b1);
            int priority2 = getBookingPriority(b2);
            return Integer.compare(priority1, priority2);
        });

        // Take first 10
        if (pendingTasks.size() > 10) {
            pendingTasks = pendingTasks.subList(0, 10);
        }

        bookingAdapter.updateBookings(pendingTasks);
        tvPendingTasksCount.setText(String.valueOf(pendingTasks.size()));
    }

    private int getBookingPriority(Booking booking) {
        if (Booking.BookingStatus.CHECKED_IN.equals(booking.getStatus())) {
            return 1; // Check-out highest priority
        } else if (Booking.BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            return 2; // Check-in second priority
        } else {
            return 3; // Pending third priority
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

        Intent intent = new Intent(ReceptionistDashboardActivity.this, LoginActivity.class);
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

