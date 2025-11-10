package com.example.projectprmt5;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projectprmt5.repository.BookingRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RevenueReportActivity extends AppCompatActivity {

    private BookingRepository bookingRepository;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private TextView tvStartDate;
    private TextView tvEndDate;
    private Button btnApply;

    private TextView tvRevenueSummary;

    private Date startDate;
    private Date endDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Revenue Report");
        }

        bookingRepository = new BookingRepository(getApplication());

        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnApply = findViewById(R.id.btnApply);
        tvRevenueSummary = findViewById(R.id.tvRevenueSummary);

        initDefaultRange();
        setupDatePickers();

        btnApply.setOnClickListener(v -> loadRevenue());

        loadRevenue();
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

    private void loadRevenue() {
        if (startDate == null || endDate == null) return;

        // Load all bookings and filter by date range
        bookingRepository.getAllBookings().observe(this, allBookings -> {
            if (allBookings == null) {
                tvRevenueSummary.setText("Không có dữ liệu");
                return;
            }

            // Filter bookings in date range and calculate revenue
            double totalRevenue = 0.0;
            int bookingCount = 0;

            for (com.example.projectprmt5.database.entities.Booking booking : allBookings) {
                Date bookingDate = booking.getBookingDate();
                if (bookingDate != null && !bookingDate.before(startDate) && !bookingDate.after(endDate)) {
                    // Only count confirmed/checked-in/checked-out bookings
                    if (com.example.projectprmt5.database.entities.Booking.BookingStatus.CONFIRMED.equals(booking.getStatus()) ||
                        com.example.projectprmt5.database.entities.Booking.BookingStatus.CHECKED_IN.equals(booking.getStatus()) ||
                        com.example.projectprmt5.database.entities.Booking.BookingStatus.CHECKED_OUT.equals(booking.getStatus())) {
                        totalRevenue += booking.getTotalAmount();
                        bookingCount++;
                    }
                }
            }

            final double finalRevenue = totalRevenue;
            final int finalCount = bookingCount;

            String text = "Tổng doanh thu: " + String.format(Locale.getDefault(), "%,.0f VNĐ", finalRevenue) +
                    "\nSố booking: " + finalCount +
                    "\nKhoảng thời gian: " + dateFormatter.format(startDate) + " - " + dateFormatter.format(endDate);
            tvRevenueSummary.setText(text);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
