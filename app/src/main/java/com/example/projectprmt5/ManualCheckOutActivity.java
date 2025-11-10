package com.example.projectprmt5;

import android.content.Intent;
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
import com.example.projectprmt5.repository.BookingRepository;
import com.example.projectprmt5.repository.RoomRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ManualCheckOutActivity
 * Màn hình cho lễ tân thực hiện check-out cho khách
 */
public class ManualCheckOutActivity extends AppCompatActivity {

    private TextInputEditText editRoomNumber;
    private Button btnCheckOut;

    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;

    private int receptionistId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_check_out);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Check-out Khách");
        }

        // Get receptionist ID from SharedPreferences
        receptionistId = getSharedPreferences("HotelManagerPrefs", MODE_PRIVATE)
                .getInt("userId", -1);

        initViews();
        initRepositories();
        setupListeners();
    }

    private void initViews() {
        editRoomNumber = findViewById(R.id.edit_room_number);
        btnCheckOut = findViewById(R.id.btn_check_out);
    }

    private void initRepositories() {
        bookingRepository = new BookingRepository(getApplication());
        roomRepository = new RoomRepository(getApplication());
    }

    private void setupListeners() {
        btnCheckOut.setOnClickListener(v -> performCheckOut());
    }

    private void performCheckOut() {
        String roomNumber = editRoomNumber.getText().toString().trim();

        if (TextUtils.isEmpty(roomNumber)) {
            Toast.makeText(this, "Vui lòng nhập số phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button
        btnCheckOut.setEnabled(false);

        // Process in background
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // 1. Find room by room number
                Room room = roomRepository.getRoomByNumber(roomNumber).get();
                if (room == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không tìm thấy phòng: " + roomNumber, Toast.LENGTH_SHORT).show();
                        btnCheckOut.setEnabled(true);
                    });
                    return;
                }

                // 2. Find active booking for this room
                List<Booking> allBookings = bookingRepository.getAllBookingsSync().get();
                Booking activeBooking = null;

                for (Booking booking : allBookings) {
                    if (booking.getRoomId() == room.getId() &&
                        Booking.BookingStatus.CHECKED_IN.equals(booking.getStatus())) {
                        activeBooking = booking;
                        break;
                    }
                }

                if (activeBooking == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không có khách đang ở phòng " + roomNumber, Toast.LENGTH_SHORT).show();
                        btnCheckOut.setEnabled(true);
                    });
                    return;
                }

                // 3. Update booking status
                activeBooking.setStatus(Booking.BookingStatus.CHECKED_OUT);
                activeBooking.setActualCheckOutTime(new Date());
                activeBooking.setCheckedOutByUserId(receptionistId);
                bookingRepository.update(activeBooking);

                // 4. Update room status to AVAILABLE
                room.setStatus(Room.RoomStatus.AVAILABLE);
                roomRepository.update(room);

                // 5. Navigate to Payment Activity
                final Booking finalBooking = activeBooking;
                runOnUiThread(() -> {
                    Toast.makeText(this, "Check-out thành công! Chuyển đến thanh toán...", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ManualCheckOutActivity.this, PaymentActivity.class);
                    intent.putExtra(PaymentActivity.EXTRA_BOOKING_ID, finalBooking.getBookingId());
                    intent.putExtra(PaymentActivity.EXTRA_AMOUNT, finalBooking.getTotalAmount());
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnCheckOut.setEnabled(true);
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
