package com.example.projectprmt5;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprmt5.database.AppDatabase;
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
        
        // Create simple layout
        androidx.constraintlayout.widget.ConstraintLayout layout = 
            new androidx.constraintlayout.widget.ConstraintLayout(this);
        
        // ScrollView for long content
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        scrollView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        ));
        
        // Linear layout inside ScrollView
        android.widget.LinearLayout linearLayout = new android.widget.LinearLayout(this);
        linearLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        linearLayout.setPadding(32, 32, 32, 32);
        
        // Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText("🔍 DATABASE DEBUG TOOL");
        tvTitle.setTextSize(24);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setPadding(0, 0, 0, 32);
        linearLayout.addView(tvTitle);
        
        // Refresh button
        btnRefresh = new Button(this);
        btnRefresh.setText("🔄 Refresh Database Info");
        btnRefresh.setOnClickListener(v -> loadDatabaseInfo());
        linearLayout.addView(btnRefresh);
        
        // Clear DB button
        btnClearDb = new Button(this);
        btnClearDb.setText("🗑️ Clear Database (Delete & Recreate)");
        btnClearDb.setOnClickListener(v -> clearDatabase());
        linearLayout.addView(btnClearDb);
        
        // Debug info text
        tvDebugInfo = new TextView(this);
        tvDebugInfo.setTextSize(12);
        tvDebugInfo.setTypeface(android.graphics.Typeface.MONOSPACE);
        tvDebugInfo.setPadding(0, 32, 0, 0);
        linearLayout.addView(tvDebugInfo);
        
        scrollView.addView(linearLayout);
        setContentView(scrollView);
        
        // Initialize database
        database = AppDatabase.getInstance(getApplicationContext());
        
        // Load info
        loadDatabaseInfo();
    }

    private void loadDatabaseInfo() {
        Log.d(TAG, "========== DATABASE DEBUG START ==========");
        
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Get all users
                List<User> users = database.userDao().getAllUsersSync();
                
                Log.d(TAG, "Total users in database: " + users.size());
                
                StringBuilder info = new StringBuilder();
                info.append("📊 DATABASE INFO\n");
                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                info.append("📁 Database: hotel_management_db\n");
                info.append("👥 Total Users: ").append(users.size()).append("\n\n");
                
                if (users.isEmpty()) {
                    info.append("⚠️ WARNING: Database is EMPTY!\n");
                    info.append("Bạn cần clear data app để database tự tạo lại.\n\n");
                } else {
                    info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    info.append("👤 ALL USERS IN DATABASE:\n");
                    info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                    
                    for (int i = 0; i < users.size(); i++) {
                        User user = users.get(i);
                        info.append("User #").append(i + 1).append("\n");
                        info.append("─────────────────────────────\n");
                        info.append("ID: ").append(user.getUserId()).append("\n");
                        info.append("Email: ").append(user.getEmail()).append("\n");
                        info.append("Full Name: ").append(user.getFullName()).append("\n");
                        info.append("Role: ").append(user.getRole()).append("\n");
                        info.append("Password Hash: ").append(user.getPasswordHash()).append("\n");
                        info.append("Is Active: ").append(user.isActive() ? "✅ Yes" : "❌ No").append("\n");
                        info.append("Phone: ").append(user.getPhoneNumber()).append("\n");
                        info.append("\n");
                        
                        // Log to console
                        Log.d(TAG, "User " + (i+1) + ": " + user.getEmail());
                        Log.d(TAG, "  - Password: " + user.getPasswordHash());
                        Log.d(TAG, "  - Role: " + user.getRole());
                        Log.d(TAG, "  - Active: " + user.isActive());
                    }
                }
                
                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                info.append("🔐 PASSWORD HASHING INFO:\n");
                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                info.append("Format: HASH_[password]\n");
                info.append("Example:\n");
                info.append("  Input: Admin123!\n");
                info.append("  Stored: HASH_Admin123!\n\n");
                
                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                info.append("📝 TEST CREDENTIALS:\n");
                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                info.append("Manager:\n");
                info.append("  Email: admin@hotel.com\n");
                info.append("  Password: Admin123!\n\n");
                info.append("Receptionist:\n");
                info.append("  Email: receptionist@hotel.com\n");
                info.append("  Password: Receptionist123!\n\n");
                info.append("Guest:\n");
                info.append("  Email: guest@example.com\n");
                info.append("  Password: Guest123!\n\n");
                
                String finalInfo = info.toString();
                
                runOnUiThread(() -> {
                    tvDebugInfo.setText(finalInfo);
                    Toast.makeText(this, "✅ Loaded " + users.size() + " users", Toast.LENGTH_SHORT).show();
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading database info: " + e.getMessage());
                e.printStackTrace();
                
                runOnUiThread(() -> {
                    tvDebugInfo.setText("❌ ERROR:\n" + e.getMessage());
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
        
        Log.d(TAG, "========== DATABASE DEBUG END ==========");
    }

    private void clearDatabase() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("⚠️ Xác nhận")
            .setMessage("Bạn có chắc muốn XÓA toàn bộ database?\n\n" +
                       "Database sẽ được tạo lại với dữ liệu mẫu mới.")
            .setPositiveButton("Xóa", (dialog, which) -> {
                // Delete database
                deleteDatabase("hotel_management_db");
                
                Toast.makeText(this, "✅ Database đã xóa!\nVui lòng khởi động lại app.", 
                             Toast.LENGTH_LONG).show();
                
                // Restart app
                android.os.Process.killProcess(android.os.Process.myPid());
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}


