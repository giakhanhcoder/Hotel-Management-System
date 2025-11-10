package com.example.projectprmt5;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.repository.BookingRepository;
import com.example.projectprmt5.repository.RoomRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OccupancyReportActivity extends AppCompatActivity {

    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private TextView tvStartDate;
    private TextView tvEndDate;
    private Button btnApply;
    private TextView tvOccupancySummary;

    private Date startDate;
    private Date endDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupancy_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Occupancy Report");
        }

        roomRepository = new RoomRepository(getApplication());
        bookingRepository = new BookingRepository(getApplication());

        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnApply = findViewById(R.id.btnApply);
        tvOccupancySummary = findViewById(R.id.tvOccupancySummary);

        initDefaultRange();
        setupDatePickers();

        btnApply.setOnClickListener(v -> calculateOccupancy());

        calculateOccupancy();
    }

    private void initDefaultRange() {
        Calendar cal = Calendar.getInstance();
        endDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        startDate = cal.getTime();

        tvStartDate.setText(dateFormatter.format(startDate));
        tvEndDate.setText(dateFormatter.format(endDate));
    }

    private void setupDatePickers() {
        tvStartDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate != null ? startDate : new Date());
            new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth, 0, 0, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    startDate = c.getTime();
                    tvStartDate.setText(dateFormatter.format(startDate));
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        tvEndDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate != null ? endDate : new Date());
            new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth, 23, 59, 59);
                    c.set(Calendar.MILLISECOND, 999);
                    endDate = c.getTime();
                    tvEndDate.setText(dateFormatter.format(endDate));
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void calculateOccupancy() {
        if (startDate == null || endDate == null) return;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Total active rooms
                List<Room> rooms = roomRepository.getAllActiveRoomsSync().get();
                int totalRooms = rooms != null ? rooms.size() : 0;

                // Bookings in date range (by check-in date within range)
                List<Booking> bookings = bookingRepository.getBookingsInDateRangeSync(startDate, endDate).get();

                long totalDays = daysBetweenInclusive(startDate, endDate);
                double occupiedRoomDays = 0;

                if (bookings != null && totalRooms > 0 && totalDays > 0) {
                    for (Booking b : bookings) {
                        Date in = b.getCheckInDate();
                        Date out = b.getCheckOutDate();
                        if (in == null || out == null) continue;

                        // Clamp to selected range
                        Date clampedStart = in.before(startDate) ? startDate : in;
                        Date clampedEnd = out.after(endDate) ? endDate : out;

                        long nights = daysBetweenExclusive(clampedStart, clampedEnd);
                        if (nights > 0) {
                            occupiedRoomDays += nights;
                        }
                    }
                }

                double denominator = totalRooms * (double) totalDays;
                final double occupancyRate = denominator > 0 ? (occupiedRoomDays * 100.0 / denominator) : 0.0;

                // Build the summary string here (so lambda captures only the final 'summary' variable)
                String summary = "Tổng phòng hoạt động: " + totalRooms +
                        "\nSố ngày: " + totalDays +
                        "\nPhòng-đêm sử dụng: " + String.format(Locale.getDefault(), "%,.0f", occupiedRoomDays) +
                        "\nTỉ lệ Occupancy (ước lượng): " + String.format(Locale.getDefault(), "%.1f%%", occupancyRate) +
                        "\nKhoảng thời gian: " + dateFormatter.format(startDate) + " - " + dateFormatter.format(endDate);

                runOnUiThread(() -> tvOccupancySummary.setText(summary));
            } catch (Exception e) {
                runOnUiThread(() -> tvOccupancySummary.setText("Không thể tải dữ liệu."));
            }
        });
    }

    private long daysBetweenInclusive(Date start, Date end) {
        Calendar s = Calendar.getInstance();
        s.setTime(start);
        s.set(Calendar.HOUR_OF_DAY, 0);
        s.set(Calendar.MINUTE, 0);
        s.set(Calendar.SECOND, 0);
        s.set(Calendar.MILLISECOND, 0);

        Calendar e = Calendar.getInstance();
        e.setTime(end);
        e.set(Calendar.HOUR_OF_DAY, 0);
        e.set(Calendar.MINUTE, 0);
        e.set(Calendar.SECOND, 0);
        e.set(Calendar.MILLISECOND, 0);

        long diff = e.getTimeInMillis() - s.getTimeInMillis();
        return (diff / (24 * 60 * 60 * 1000)) + 1;
    }

    private long daysBetweenExclusive(Date start, Date end) {
        // count nights between start (check-in) and end (check-out), typical hotel convention
        Calendar s = Calendar.getInstance();
        s.setTime(start);
        s.set(Calendar.HOUR_OF_DAY, 0);
        s.set(Calendar.MINUTE, 0);
        s.set(Calendar.SECOND, 0);
        s.set(Calendar.MILLISECOND, 0);

        Calendar e = Calendar.getInstance();
        e.setTime(end);
        e.set(Calendar.HOUR_OF_DAY, 0);
        e.set(Calendar.MINUTE, 0);
        e.set(Calendar.SECOND, 0);
        e.set(Calendar.MILLISECOND, 0);

        long diff = e.getTimeInMillis() - s.getTimeInMillis();
        long days = diff / (24 * 60 * 60 * 1000);
        return Math.max(days, 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
