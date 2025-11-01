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
    private Button btnCheckIn, btnCancel, btnEdit, btnDelete, btnCheckoutAndBilling;

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
        btnCheckoutAndBilling = findViewById(R.id.btn_checkout_and_billing);
        btnCancel = findViewById(R.id.btn_cancel_booking);
        btnEdit = findViewById(R.id.btn_edit_booking);
        btnDelete = findViewById(R.id.btn_delete_booking);
    }

    private void updateUI(Booking booking) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

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

        // --- Dynamically show/hide buttons based on booking status ---
        String status = booking.getStatus();

        // Default to all buttons hidden, then show them based on status
        btnCheckIn.setVisibility(View.GONE);
        btnCheckoutAndBilling.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        switch (status) {
            case Booking.BookingStatus.PENDING:
                btnCheckIn.setVisibility(View.VISIBLE); // Show Check-In for PENDING status
                btnCheckoutAndBilling.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                break;
            case Booking.BookingStatus.CONFIRMED:
                btnCheckIn.setVisibility(View.VISIBLE);
                btnCheckoutAndBilling.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                break;
            case Booking.BookingStatus.CHECKED_IN:
                btnCheckoutAndBilling.setVisibility(View.VISIBLE);
                break;
            case Booking.BookingStatus.CHECKED_OUT:
            case Booking.BookingStatus.CANCELLED:
                // All major action buttons remain GONE
                break;
        }
    }

    private void setupButtonClickListeners() {
        btnCheckIn.setOnClickListener(v -> {
            bookingViewModel.updateBookingStatus(bookingId, Booking.BookingStatus.CHECKED_IN);
            Toast.makeText(this, "Checked-In Successfully!", Toast.LENGTH_SHORT).show();
        });

        btnCheckoutAndBilling.setOnClickListener(v -> {
            if (currentBooking != null) {
                Intent intent = new Intent(BookingDetailActivity.this, PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_BOOKING_ID, currentBooking.getBookingId());
                intent.putExtra(PaymentActivity.EXTRA_AMOUNT, currentBooking.getTotalAmount());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Booking details are not loaded yet. Please wait.", Toast.LENGTH_SHORT).show();
            }
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
