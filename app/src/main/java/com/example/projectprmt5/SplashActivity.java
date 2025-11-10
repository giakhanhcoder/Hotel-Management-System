package com.example.projectprmt5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.RoomRepository;

import java.util.List;

/**
 * Màn hình khởi động
 * Splash Screen
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DELAY = 2000; // 2 giây
    private static final String PREFS_NAME = "HotelManagerPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_ROLE = "userRole";

    private TextView tvTotalRooms;
    private TextView tvAvailableRooms;
    private RoomRepository roomRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d(TAG, "Splash screen started");

        // Initialize views
        tvTotalRooms = findViewById(R.id.tvTotalRooms);
        tvAvailableRooms = findViewById(R.id.tvAvailableRooms);

        // Initialize repository
        // roomRepository = new RoomRepository(getApplication());

        // Load statistics
        // loadHotelStatistics();

        // Delay để hiển thị splash screen
        new Handler().postDelayed(() -> {
            checkLoginStatusAndNavigate();
        }, SPLASH_DELAY);
    }

    /**
     * Load hotel statistics để hiển thị trên splash
     */
    private void loadHotelStatistics() {
        /*
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Get total rooms
                List<Room> allRooms = roomRepository.getAllActiveRoomsSync().get();
                int totalRooms = allRooms != null ? allRooms.size() : 0;

                // Get available rooms
                List<Room> availableRooms = roomRepository.getRoomsByStatusSync(Room.RoomStatus.AVAILABLE).get();
                int availableCount = availableRooms != null ? availableRooms.size() : 0;

                runOnUiThread(() -> {
                    tvTotalRooms.setText(totalRooms + "+ Phòng");
                    tvAvailableRooms.setText(availableCount + " Sẵn sàng");
                });

            } catch (Exception e) {
                Log.e(TAG, "Error loading statistics: " + e.getMessage());
                // Set default values on error
                runOnUiThread(() -> {
                    tvTotalRooms.setText("50+ Phòng");
                    tvAvailableRooms.setText("15 Sẵn sàng");
                });
            }
        });
        */
    }

    /**
     * Kiểm tra trạng thái đăng nhập và chuyển màn hình
     */
    private void checkLoginStatusAndNavigate() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);

        Log.d(TAG, "Checking login status: " + isLoggedIn);

        if (isLoggedIn) {
            // User đã đăng nhập -> chuyển đến Dashboard theo role
            String role = prefs.getString(KEY_USER_ROLE, "");
            Log.d(TAG, "User is logged in with role: " + role);

            // Navigate to appropriate Dashboard based on role
            Intent intent = null;
            switch (role) {
                case User.Role.GUEST:
                    intent = new Intent(SplashActivity.this, GuestDashboardActivity.class);
                    break;
                case User.Role.RECEPTIONIST:
                    intent = new Intent(SplashActivity.this, ReceptionistDashboardActivity.class);
                    break;
            case User.Role.MANAGER:
                intent = new Intent(SplashActivity.this, ManagerDashboardActivity.class);
                break;
                default:
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    break;
            }
            startActivity(intent);
        } else {
            // User chưa đăng nhập -> chuyển đến WelcomeActivity
            Log.d(TAG, "User not logged in, navigating to Welcome");
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }

        // Đóng SplashActivity
        finish();
    }
}
