package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.viewmodel.BookingViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "com.example.projectprmt5.EXTRA_BOOKING_ID";

    private BookingViewModel bookingViewModel;
    private Booking currentBooking;
    private int bookingId;

    private TextView textViewBookingCode, textViewBookingStatus, textViewRoomId, textViewGuestId,
            textViewCheckInDate, textViewCheckOutDate, textViewNumGuests, textViewTotalAmount;
    private Button btnCheckIn, btnCheckOut, btnCancel, btnEdit, btnDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Booking Details");
        }

        initViews();

        bookingId = getIntent().getIntExtra(EXTRA_BOOKING_ID, -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Error: Invalid Booking ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        bookingViewModel.getBookingById(bookingId).observe(this, booking -> {
            if (booking != null) {
                currentBooking = booking;
                updateUI(currentBooking);
            }
        });

        setupButtonClickListeners();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        textViewBookingCode = findViewById(R.id.text_view_booking_code);
        textViewBookingStatus = findViewById(R.id.text_view_booking_status);
        textViewRoomId = findViewById(R.id.text_view_room_id);
        textViewGuestId = findViewById(R.id.text_view_guest_id);
        textViewCheckInDate = findViewById(R.id.text_view_check_in_date);
        textViewCheckOutDate = findViewById(R.id.text_view_check_out_date);
        textViewNumGuests = findViewById(R.id.text_view_number_of_guests);
        textViewTotalAmount = findViewById(R.id.text_view_total_amount);

        btnCheckIn = findViewById(R.id.btn_check_in);
        btnCheckOut = findViewById(R.id.btn_check_out);
        btnCancel = findViewById(R.id.btn_cancel_booking);
        btnEdit = findViewById(R.id.btn_edit_booking);
        btnDelete = findViewById(R.id.btn_delete_booking);
    }

    private void updateUI(Booking booking) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Update the toolbar title with the booking code
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle("Code: " + booking.getBookingCode());
        }

        textViewBookingStatus.setText("Status: " + booking.getStatus());
        textViewRoomId.setText("Room ID: " + booking.getRoomId());
        textViewGuestId.setText("Guest ID: " + booking.getGuestId());
        textViewCheckInDate.setText("Check-in: " + dateFormat.format(booking.getCheckInDate()));
        textViewCheckOutDate.setText("Check-out: " + dateFormat.format(booking.getCheckOutDate()));
        textViewNumGuests.setText("Guests: " + booking.getNumberOfGuests());
        textViewTotalAmount.setText("Total: " + String.format(Locale.getDefault(), "%.2f", booking.getTotalAmount()));


    }

    private void setupButtonClickListeners() {
        btnCheckIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckInActivity.class);
            intent.putExtra(CheckInActivity.EXTRA_BOOKING_ID, bookingId);
            startActivity(intent);
        });

        btnCheckOut.setOnClickListener(v -> {
            bookingViewModel.checkOut(bookingId);
            Toast.makeText(this, "Checked-Out!", Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> {
            bookingViewModel.updateBookingStatus(bookingId, Booking.BookingStatus.CANCELLED);
            Toast.makeText(this, "Booking Cancelled!", Toast.LENGTH_SHORT).show();
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this booking?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (currentBooking != null) {
                        bookingViewModel.delete(currentBooking);
                        Toast.makeText(this, "Booking Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error: Booking data not loaded yet.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBookingActivity.class);
            intent.putExtra(AddBookingActivity.EXTRA_BOOKING_ID, bookingId);
            startActivity(intent); 
        });

    }
}
