package com.example.projectprmt5;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * ManualCheckInActivity
 * Màn hình cho lễ tân thực hiện check-in cho khách hàng walk-in (không dùng app)
 */
public class ManualCheckInActivity extends AppCompatActivity {

    private TextInputEditText editGuestName;
    private TextInputEditText editGuestPhone;
    private TextInputEditText editGuestEmail;
    private TextInputEditText editRoomNumber;
    private TextInputEditText editNumGuests;
    private TextInputEditText editNumNights;
    private Button btnCheckIn;

    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;

    private int receptionistId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_check_in);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Check-in Khách Walk-in");
        }

        // Get receptionist ID from SharedPreferences
        receptionistId = getSharedPreferences("HotelManagerPrefs", MODE_PRIVATE)
                .getInt("userId", -1);

        initViews();
        initRepositories();
        setupListeners();
    }

    private void initViews() {
        editGuestName = findViewById(R.id.edit_guest_name);
        editGuestPhone = findViewById(R.id.edit_guest_phone);
        editGuestEmail = findViewById(R.id.edit_guest_email);
        editRoomNumber = findViewById(R.id.edit_room_number);
        editNumGuests = findViewById(R.id.edit_num_guests);
        editNumNights = findViewById(R.id.edit_num_nights);
        btnCheckIn = findViewById(R.id.btn_check_in);
    }

    private void initRepositories() {
        userRepository = new UserRepository(getApplication());
        bookingRepository = new BookingRepository(getApplication());
        roomRepository = new RoomRepository(getApplication());
    }

    private void setupListeners() {
        btnCheckIn.setOnClickListener(v -> performCheckIn());
    }

    private void performCheckIn() {
        String guestName = editGuestName.getText().toString().trim();
        String guestPhone = editGuestPhone.getText().toString().trim();
        String guestEmail = editGuestEmail.getText().toString().trim();
        String roomNumber = editRoomNumber.getText().toString().trim();
        String numGuestsStr = editNumGuests.getText().toString().trim();
        String numNightsStr = editNumNights.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(guestName) || TextUtils.isEmpty(guestPhone) ||
            TextUtils.isEmpty(roomNumber) || TextUtils.isEmpty(numGuestsStr) ||
            TextUtils.isEmpty(numNightsStr)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int numGuests;
        int numNights;
        try {
            numGuests = Integer.parseInt(numGuestsStr);
            numNights = Integer.parseInt(numNightsStr);

            if (numGuests < 1 || numGuests > 10) {
                Toast.makeText(this, "Số khách phải từ 1-10", Toast.LENGTH_SHORT).show();
                return;
            }
            if (numNights < 1) {
                Toast.makeText(this, "Số đêm phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số khách và số đêm phải là số", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent double submission
        btnCheckIn.setEnabled(false);

        // Process in background
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // 1. Find room by room number
                Room room = roomRepository.getRoomByNumber(roomNumber).get();
                if (room == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không tìm thấy phòng: " + roomNumber, Toast.LENGTH_SHORT).show();
                        btnCheckIn.setEnabled(true);
                    });
                    return;
                }

                // 2. Check if room is available
                if (!Room.RoomStatus.AVAILABLE.equals(room.getStatus())) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Phòng " + roomNumber + " không khả dụng", Toast.LENGTH_SHORT).show();
                        btnCheckIn.setEnabled(true);
                    });
                    return;
                }

                // 3. Create or find guest user
                User guest = userRepository.getUserByUsernameSync(guestPhone).get();
                if (guest == null) {
                    // Create new guest account
                    guest = new User();
                    guest.setEmail(guestPhone);
                    guest.setPasswordHash("guest123"); // Default password
                    guest.setFullName(guestName);
                    guest.setEmail(guestEmail.isEmpty() ? guestPhone + "@guest.com" : guestEmail);
                    guest.setPhoneNumber(guestPhone);
                    guest.setRole(User.Role.GUEST);

                    long guestId = userRepository.insertSync(guest).get();
                    guest.setUserId((int) guestId);
                }

                // 4. Calculate dates and amount
                Date checkInDate = new Date(); // Now
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, numNights);
                Date checkOutDate = cal.getTime();

                double totalAmount = room.getPrice() * numNights;

                // 5. Create booking
                Booking booking = new Booking(
                    guest.getUserId(),
                    room.getId(),
                    checkInDate,
                    checkOutDate,
                    numGuests,
                    totalAmount
                );
                booking.setStatus(Booking.BookingStatus.CHECKED_IN);
                booking.setActualCheckInTime(new Date());
                booking.setCheckedInByUserId(receptionistId);

                long bookingId = bookingRepository.insert(booking).get();

                // 6. Update room status to OCCUPIED
                room.setStatus(Room.RoomStatus.OCCUPIED);
                roomRepository.update(room);

                // 7. Show success message
                runOnUiThread(() -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String message = "Check-in thành công!\n" +
                            "Khách: " + guestName + "\n" +
                            "Phòng: " + roomNumber + "\n" +
                            "Từ: " + dateFormat.format(checkInDate) + "\n" +
                            "Đến: " + dateFormat.format(checkOutDate) + "\n" +
                            "Tổng tiền: " + String.format(Locale.getDefault(), "%,.0f VNĐ", totalAmount);

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnCheckIn.setEnabled(true);
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
