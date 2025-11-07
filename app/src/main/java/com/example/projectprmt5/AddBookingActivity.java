package com.example.projectprmt5;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.viewmodel.BookingViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AddBookingActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "com.example.projectprmt5.EDIT_BOOKING_ID";

    private TextInputEditText editTextRoomId, editTextCheckIn, editTextCheckOut, editTextNumGuests;
    private TextInputLayout guestIdLayout;
    private Button buttonSave;

    private BookingViewModel bookingViewModel;
    private int currentBookingId = -1;
    private Booking currentBooking;
    private int loggedInGuestId = -1;

    private static final String PREF_NAME = "HotelManagerPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loggedInGuestId = prefs.getInt(KEY_USER_ID, -1);

        initViews();

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        if (getIntent().hasExtra(EXTRA_BOOKING_ID)) {
            currentBookingId = getIntent().getIntExtra(EXTRA_BOOKING_ID, -1);
            setTitle("Edit Booking");
            buttonSave.setText("Update Booking");
            if (guestIdLayout != null) {
                guestIdLayout.setVisibility(View.GONE);
            }

            bookingViewModel.getBookingById(currentBookingId).observe(this, booking -> {
                if (booking != null) {
                    currentBooking = booking;
                    populateFields(booking);
                }
            });
        } else {
            setTitle("Create New Booking");
            buttonSave.setText("Save Booking");
            if (guestIdLayout != null) {
                guestIdLayout.setVisibility(View.GONE);
            }
        }

        buttonSave.setOnClickListener(v -> saveBooking());
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
        editTextRoomId = findViewById(R.id.edit_text_room_id);
        guestIdLayout = findViewById(R.id.guest_id_layout); // Assuming a TextInputLayout wrapper
        editTextCheckIn = findViewById(R.id.edit_text_check_in);
        editTextCheckOut = findViewById(R.id.edit_text_check_out);
        editTextNumGuests = findViewById(R.id.edit_text_num_guests);
        buttonSave = findViewById(R.id.button_save_booking);
    }

    private void populateFields(Booking booking) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editTextRoomId.setText(String.valueOf(booking.getRoomId()));
        editTextNumGuests.setText(String.valueOf(booking.getNumberOfGuests()));
        editTextCheckIn.setText(dateFormat.format(booking.getCheckInDate()));
        editTextCheckOut.setText(dateFormat.format(booking.getCheckOutDate()));
    }

    private void saveBooking() {
        String roomIdStr = editTextRoomId.getText().toString();
        String checkInStr = editTextCheckIn.getText().toString();
        String checkOutStr = editTextCheckOut.getText().toString();
        String numGuestsStr = editTextNumGuests.getText().toString();

        if (TextUtils.isEmpty(roomIdStr) || TextUtils.isEmpty(checkInStr) ||
            TextUtils.isEmpty(checkOutStr) || TextUtils.isEmpty(numGuestsStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (loggedInGuestId == -1 && currentBookingId == -1) {
            Toast.makeText(this, "Could not identify user. Please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            int numGuests = Integer.parseInt(numGuestsStr);
            if (numGuests < 1 || numGuests > 30) {
                Toast.makeText(this, "Number of guests must be between 1 and 30.", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setLenient(false);
            Date checkInDate = dateFormat.parse(checkInStr);
            Date checkOutDate = dateFormat.parse(checkOutStr);

            // Date validation
            Calendar calToday = Calendar.getInstance();
            calToday.set(Calendar.HOUR_OF_DAY, 0);
            calToday.set(Calendar.MINUTE, 0);
            calToday.set(Calendar.SECOND, 0);
            calToday.set(Calendar.MILLISECOND, 0);
            Date today = calToday.getTime();

            Calendar cal3Months = Calendar.getInstance();
            cal3Months.add(Calendar.MONTH, 3);
            Date threeMonthsLater = cal3Months.getTime();

            if (checkInDate.before(today)) {
                Toast.makeText(this, "Check-in date cannot be in the past.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkOutDate.before(checkInDate) || checkOutDate.equals(checkInDate)) {
                Toast.makeText(this, "Check-out date must be after the check-in date.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkOutDate.after(threeMonthsLater)) {
                Toast.makeText(this, "Check-out date cannot be more than 3 months from today.", Toast.LENGTH_SHORT).show();
                return;
            }

            int roomId = Integer.parseInt(roomIdStr);

            boolean isAvailable = bookingViewModel.isRoomAvailable(roomId, checkInDate, checkOutDate).get();
            if (!isAvailable) {
                Toast.makeText(this, "This room is already booked for the selected dates.", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalAmount = 100.0; // Placeholder

            if (currentBookingId != -1) {
                currentBooking.setRoomId(roomId);
                currentBooking.setCheckInDate(checkInDate);
                currentBooking.setCheckOutDate(checkOutDate);
                currentBooking.setNumberOfGuests(numGuests);
                currentBooking.setTotalAmount(totalAmount);
                bookingViewModel.update(currentBooking);
                Toast.makeText(this, "Booking updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Booking newBooking = new Booking(loggedInGuestId, roomId, checkInDate, checkOutDate, numGuests, totalAmount);
                bookingViewModel.insert(newBooking);
                Toast.makeText(this, "Booking saved successfully!", Toast.LENGTH_SHORT).show();
            }

            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for Room ID and guest count.", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(this, "Please use yyyy-MM-dd format for dates and ensure they are valid.", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, "Error checking room availability.", Toast.LENGTH_SHORT).show();
        }
    }
}
