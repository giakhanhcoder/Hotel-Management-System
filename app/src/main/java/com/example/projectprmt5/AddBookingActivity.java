package com.example.projectprmt5;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.viewmodel.BookingViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBookingActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "com.example.projectprmt5.EDIT_BOOKING_ID";

    private TextInputEditText editTextRoomId, editTextGuestId, editTextCheckIn, editTextCheckOut, editTextNumGuests;
    private Button buttonSave;

    private BookingViewModel bookingViewModel;
    private int currentBookingId = -1;
    private Booking currentBooking;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        // Add back button to ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        // Check if we are in "Edit Mode"
        if (getIntent().hasExtra(EXTRA_BOOKING_ID)) {
            currentBookingId = getIntent().getIntExtra(EXTRA_BOOKING_ID, -1);
            setTitle("Edit Booking");
            buttonSave.setText("Update Booking");

            bookingViewModel.getBookingById(currentBookingId).observe(this, booking -> {
                if (booking != null) {
                    currentBooking = booking;
                    populateFields(booking);
                }
            });

        } else {
            setTitle("Create New Booking");
            buttonSave.setText("Save Booking");
        }

        buttonSave.setOnClickListener(v -> saveBooking());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        editTextRoomId = findViewById(R.id.edit_text_room_id);
        editTextGuestId = findViewById(R.id.edit_text_guest_id);
        editTextCheckIn = findViewById(R.id.edit_text_check_in);
        editTextCheckOut = findViewById(R.id.edit_text_check_out);
        editTextNumGuests = findViewById(R.id.edit_text_num_guests);
        buttonSave = findViewById(R.id.button_save_booking);
    }

    private void populateFields(Booking booking) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editTextRoomId.setText(String.valueOf(booking.getRoomId()));
        editTextGuestId.setText(String.valueOf(booking.getGuestId()));
        editTextNumGuests.setText(String.valueOf(booking.getNumberOfGuests()));
        editTextCheckIn.setText(dateFormat.format(booking.getCheckInDate()));
        editTextCheckOut.setText(dateFormat.format(booking.getCheckOutDate()));
    }

    private void saveBooking() {
        String roomIdStr = editTextRoomId.getText().toString();
        String guestIdStr = editTextGuestId.getText().toString();
        String checkInStr = editTextCheckIn.getText().toString();
        String checkOutStr = editTextCheckOut.getText().toString();
        String numGuestsStr = editTextNumGuests.getText().toString();

        if (TextUtils.isEmpty(roomIdStr) || TextUtils.isEmpty(guestIdStr) ||
            TextUtils.isEmpty(checkInStr) || TextUtils.isEmpty(checkOutStr) ||
            TextUtils.isEmpty(numGuestsStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int roomId = Integer.parseInt(roomIdStr);
            int guestId = Integer.parseInt(guestIdStr);
            int numGuests = Integer.parseInt(numGuestsStr);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date checkInDate = dateFormat.parse(checkInStr);
            Date checkOutDate = dateFormat.parse(checkOutStr);

            // For simplicity, total amount is hardcoded. In a real app, this would be calculated.
            double totalAmount = 100.0;

            if (currentBookingId != -1) {
                // Update existing booking
                currentBooking.setRoomId(roomId);
                currentBooking.setGuestId(guestId);
                currentBooking.setCheckInDate(checkInDate);
                currentBooking.setCheckOutDate(checkOutDate);
                currentBooking.setNumberOfGuests(numGuests);
                currentBooking.setTotalAmount(totalAmount);
                bookingViewModel.update(currentBooking);
                Toast.makeText(this, "Booking updated successfully!", Toast.LENGTH_SHORT).show();

            } else {
                // Create new booking
                Booking newBooking = new Booking(guestId, roomId, checkInDate, checkOutDate, numGuests, totalAmount);
                bookingViewModel.insert(newBooking);
                Toast.makeText(this, "Booking saved successfully!", Toast.LENGTH_SHORT).show();
            }

            finish(); // Go back to the previous screen

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for IDs and guest count", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(this, "Please use yyyy-MM-dd format for dates", Toast.LENGTH_SHORT).show();
        }
    }
}
