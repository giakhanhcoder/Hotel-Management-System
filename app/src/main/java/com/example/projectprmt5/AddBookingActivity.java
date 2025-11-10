package com.example.projectprmt5;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.viewmodel.BookingViewModel;
import com.example.projectprmt5.viewmodel.RoomViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class AddBookingActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "com.example.projectprmt5.EDIT_BOOKING_ID";

    private TextInputEditText editTextRoomNumber, editTextCheckIn, editTextCheckOut, editTextNumGuests;
    private Button buttonSave;

    private BookingViewModel bookingViewModel;
    private RoomViewModel roomViewModel;
    private int loggedInGuestId = -1;
    private int selectedRoomId = -1;
    private Room selectedRoom;

    private static final String PREF_NAME = "HotelManagerPrefs";
    private static final String KEY_USER_ID = "userId";

    private final Executor backgroundExecutor = AppDatabase.databaseWriteExecutor;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

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
        setupDatePickers();

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("SELECTED_ROOM_ID")) {
            selectedRoomId = extras.getInt("SELECTED_ROOM_ID");
            String selectedRoomNumber = extras.getString("SELECTED_ROOM_NUMBER");
            setTitle("Book Room " + selectedRoomNumber);
            editTextRoomNumber.setText(selectedRoomNumber);
            editTextRoomNumber.setEnabled(false);

            // Fetch room details to get the price
            roomViewModel.getRoomById(selectedRoomId).observe(this, room -> {
                this.selectedRoom = room;
            });

        } else {
            setTitle("Create New Booking");
        }

        buttonSave.setOnClickListener(v -> saveBooking());
    }

    private void setupDatePickers() {
        editTextCheckIn.setOnClickListener(v -> showDatePickerDialog(editTextCheckIn));
        editTextCheckOut.setOnClickListener(v -> showDatePickerDialog(editTextCheckOut));
    }

    private void showDatePickerDialog(TextInputEditText dateEditText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateEditText.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
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
        editTextRoomNumber = findViewById(R.id.edit_text_room_id);
        editTextCheckIn = findViewById(R.id.edit_text_check_in);
        editTextCheckOut = findViewById(R.id.edit_text_check_out);
        editTextNumGuests = findViewById(R.id.edit_text_num_guests);
        buttonSave = findViewById(R.id.button_save_booking);
    }

    private void saveBooking() {
        String checkInStr = editTextCheckIn.getText().toString();
        String checkOutStr = editTextCheckOut.getText().toString();
        String numGuestsStr = editTextNumGuests.getText().toString();

        if (selectedRoom == null) {
            Toast.makeText(this, "Room data is not loaded yet. Please wait.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(checkInStr) || TextUtils.isEmpty(checkOutStr) || TextUtils.isEmpty(numGuestsStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (loggedInGuestId == -1) {
            Toast.makeText(this, "Could not identify user. Please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            int numGuests = Integer.parseInt(numGuestsStr);
            if (numGuests < 1 || numGuests > 20) {
                Toast.makeText(this, "Number of guests must be between 1 and 20.", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date checkInDate = dateFormat.parse(checkInStr);
            Date checkOutDate = dateFormat.parse(checkOutStr);

            Calendar calToday = Calendar.getInstance();
            calToday.set(Calendar.HOUR_OF_DAY, 0); calToday.set(Calendar.MINUTE, 0); calToday.set(Calendar.SECOND, 0); calToday.set(Calendar.MILLISECOND, 0);

            if (checkInDate.before(calToday.getTime())) {
                Toast.makeText(this, "Check-in date cannot be in the past.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!checkOutDate.after(checkInDate)) {
                Toast.makeText(this, "Check-out date must be after the check-in date.", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- Calculate Total Amount ---
            long diffInMillis = checkOutDate.getTime() - checkInDate.getTime();
            long numberOfNights = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            if (numberOfNights <= 0) { // Min 1 night stay
                numberOfNights = 1;
            }
            double totalAmount = selectedRoom.getPrice() * numberOfNights;

            backgroundExecutor.execute(() -> {
                try {
                    boolean isAvailable = bookingViewModel.isRoomAvailable(selectedRoomId, checkInDate, checkOutDate).get();
                    mainThreadHandler.post(() -> {
                        if (!isAvailable) {
                            Toast.makeText(AddBookingActivity.this, "This room is already booked for the selected dates.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Booking newBooking = new Booking(loggedInGuestId, selectedRoomId, checkInDate, checkOutDate, numGuests, totalAmount);
                        bookingViewModel.insert(newBooking);
                        Toast.makeText(AddBookingActivity.this, "Booking successful! Total: " + totalAmount, Toast.LENGTH_LONG).show();
                        finish();
                    });
                } catch (Exception e) {
                    mainThreadHandler.post(() -> Toast.makeText(AddBookingActivity.this, "Error during booking process: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });

        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format. Please select dates again.", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number of guests.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
        }
    }
}
