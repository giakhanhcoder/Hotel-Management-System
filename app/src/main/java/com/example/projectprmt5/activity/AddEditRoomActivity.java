package com.example.projectprmt5.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.R;
import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.RoomType;
import com.example.projectprmt5.viewmodel.RoomViewModel;
import com.example.projectprmt5.viewmodel.RoomTypeViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddEditRoomActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.projectprmt5.EXTRA_ID";

    private TextInputEditText etRoomNumber;
    private Spinner spinnerRoomType;
    private TextInputEditText etRoomPrice;
    private Button btnDelete;

    private RoomViewModel roomViewModel;
    private RoomTypeViewModel roomTypeViewModel;
    private int currentRoomId = -1;
    private Room currentRoom;
    private List<String> roomTypeNames = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room);

        etRoomNumber = findViewById(R.id.et_room_number);
        spinnerRoomType = findViewById(R.id.spinner_room_type);
        etRoomPrice = findViewById(R.id.et_room_price);
        btnDelete = findViewById(R.id.btn_delete_room);

        setupSpinner();

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_ID)) {
            setTitle("Edit Room");
            currentRoomId = extras.getInt(EXTRA_ID);
            roomViewModel.getRoomById(currentRoomId).observe(this, room -> {
                if (room != null) {
                    currentRoom = room;
                    etRoomNumber.setText(room.getRoomNumber());
                    etRoomPrice.setText(String.valueOf(room.getPrice()));
                    // Set spinner selection
                    int position = spinnerAdapter.getPosition(room.getType());
                    spinnerRoomType.setSelection(position);
                }
            });
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            setTitle("Add New Room");
            btnDelete.setVisibility(View.GONE);
        }

        final Button btnSave = findViewById(R.id.btn_save_room);
        btnSave.setOnClickListener(view -> saveRoom());

        btnDelete.setOnClickListener(view -> showDeleteConfirmationDialog());
    }

    private void setupSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomTypeNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(spinnerAdapter);

        roomTypeViewModel = new ViewModelProvider(this).get(RoomTypeViewModel.class);
        roomTypeViewModel.getAllRoomTypes().observe(this, roomTypes -> {
            roomTypeNames.clear();
            for (RoomType type : roomTypes) {
                roomTypeNames.add(type.getName());
            }
            spinnerAdapter.notifyDataSetChanged();

            // If editing, re-set selection after data is loaded
            if (currentRoom != null) {
                int position = spinnerAdapter.getPosition(currentRoom.getType());
                spinnerRoomType.setSelection(position);
            }
        });
    }

    private void saveRoom() {
        String roomNumberStr = etRoomNumber.getText().toString().trim();
        String roomPriceStr = etRoomPrice.getText().toString().trim();
        String roomTypeStr = spinnerRoomType.getSelectedItem() != null ? spinnerRoomType.getSelectedItem().toString() : null;

        if (TextUtils.isEmpty(roomNumberStr)) {
            etRoomNumber.setError("Room number is required");
            return;
        }

        if (roomTypeStr == null || TextUtils.isEmpty(roomTypeStr)) {
            Toast.makeText(this, "Please select a room type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(roomPriceStr)) {
            etRoomPrice.setError("Price is required");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(roomPriceStr);
        } catch (NumberFormatException e) {
            etRoomPrice.setError("Price must be a number");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Room existingRoom = roomViewModel.getRoomByNumberSync(roomNumberStr);
            boolean isDuplicate = existingRoom != null && existingRoom.getId() != currentRoomId;

            runOnUiThread(() -> {
                if (isDuplicate) {
                    etRoomNumber.setError("This room number already exists");
                } else {
                    if (currentRoomId != -1) {
                        Room updatedRoom = new Room(roomNumberStr, roomTypeStr, price);
                        updatedRoom.setId(currentRoomId);
                        if (currentRoom != null) {
                            updatedRoom.setStatus(currentRoom.getStatus());
                        }
                        roomViewModel.update(updatedRoom);
                        Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Room newRoom = new Room(roomNumberStr, roomTypeStr, price);
                        roomViewModel.insert(newRoom);
                        Toast.makeText(this, "Room saved", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            });
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this room?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteRoom())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteRoom() {
        if (currentRoom != null) {
            roomViewModel.delete(currentRoom);
            Toast.makeText(this, "Room deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
