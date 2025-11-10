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

        // Run in background executor
        com.example.projectprmt5.database.AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Double totalRevenue = bookingRepository.getTotalRevenueInDateRange(startDate, endDate).get();
                List<com.example.projectprmt5.database.entities.Booking> bookings =
                        bookingRepository.getBookingsInDateRangeSync(startDate, endDate).get();
                int bookingCount = bookings != null ? bookings.size() : 0;

                runOnUiThread(() -> {
                    String text = "Tổng doanh thu: " + String.format(Locale.getDefault(), "%,.0f VNĐ", totalRevenue) +
                            "\nSố booking: " + bookingCount +
                            "\nKhoảng thời gian: " + dateFormatter.format(startDate) + " - " + dateFormatter.format(endDate);
                    tvRevenueSummary.setText(text);
                });
            } catch (Exception e) {
                runOnUiThread(() -> tvRevenueSummary.setText("Không thể tải dữ liệu."));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


