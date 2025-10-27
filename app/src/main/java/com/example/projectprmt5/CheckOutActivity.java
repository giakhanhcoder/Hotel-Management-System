package com.example.projectprmt5;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.viewmodel.BookingViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CheckOutActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "com.example.projectprmt5.CHECKOUT_BOOKING_ID";

    private BookingViewModel bookingViewModel;
    private TextView summaryTextView;
    private Button confirmButton;
    private int bookingId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Check-Out Confirmation");
        }

        summaryTextView = findViewById(R.id.checkout_booking_summary);
        confirmButton = findViewById(R.id.btn_confirm_check_out);

        bookingId = getIntent().getIntExtra(EXTRA_BOOKING_ID, -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Invalid Booking ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        bookingViewModel.getBookingById(bookingId).observe(this, booking -> {
            if (booking != null) {
                populateSummary(booking);
            }
        });

        confirmButton.setOnClickListener(v -> {
            bookingViewModel.checkOut(bookingId);
            Toast.makeText(this, "Check-Out Successful!", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void populateSummary(Booking booking) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String summary = "Booking Code: " + booking.getBookingCode() + "\n"
                + "Room ID: " + booking.getRoomId() + "\n"
                + "Check-in: " + dateFormat.format(booking.getCheckInDate()) + "\n"
                + "Check-out: " + dateFormat.format(booking.getCheckOutDate());
        summaryTextView.setText(summary);
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
