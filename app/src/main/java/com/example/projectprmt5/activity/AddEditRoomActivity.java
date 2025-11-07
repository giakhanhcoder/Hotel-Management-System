package com.example.projectprmt5.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.R;
import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.viewmodel.RoomViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class AddEditRoomActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.projectprmt5.EXTRA_ID";

    private TextInputEditText etRoomNumber;
    private TextInputEditText etRoomType;
    private TextInputEditText etRoomPrice;
    private Button btnDelete;

    private RoomViewModel roomViewModel;
    private int currentRoomId = -1;
    private Room currentRoom;

    // Define allowed room types
    private static final List<String> ALLOWED_ROOM_TYPES = Arrays.asList("SINGLE", "DOUBLE", "SUITE", "PENTHOUSE");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room);

        etRoomNumber = findViewById(R.id.et_room_number);
        etRoomType = findViewById(R.id.et_room_type);
        etRoomPrice = findViewById(R.id.et_room_price);
        btnDelete = findViewById(R.id.btn_delete_room);

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_ID)) {
            setTitle("Sửa phòng");
            currentRoomId = extras.getInt(EXTRA_ID);
            roomViewModel.getRoomById(currentRoomId).observe(this, room -> {
                if (room != null) {
                    currentRoom = room;
                    etRoomNumber.setText(room.getRoomNumber());
                    etRoomType.setText(room.getType());
                    etRoomPrice.setText(String.valueOf(room.getPrice()));
                }
            });
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            setTitle("Thêm phòng mới");
            btnDelete.setVisibility(View.GONE);
        }

        final Button btnSave = findViewById(R.id.btn_save_room);
        btnSave.setOnClickListener(view -> saveRoom());

        btnDelete.setOnClickListener(view -> showDeleteConfirmationDialog());
    }

    private void saveRoom() {
        String roomNumberStr = etRoomNumber.getText().toString().trim();
        String roomTypeStr = etRoomType.getText().toString().trim().toUpperCase();
        String roomPriceStr = etRoomPrice.getText().toString().trim();

        // --- Start Validation ---
        if (TextUtils.isEmpty(roomNumberStr)) {
            etRoomNumber.setError("Số phòng là bắt buộc");
            return;
        }

        if (TextUtils.isEmpty(roomTypeStr)) {
            etRoomType.setError("Loại phòng là bắt buộc");
            return;
        } else if (!ALLOWED_ROOM_TYPES.contains(roomTypeStr)) {
            etRoomType.setError("Loại phòng phải là một trong: SINGLE, DOUBLE, SUITE, PENTHOUSE");
            return;
        }

        if (TextUtils.isEmpty(roomPriceStr)) {
            etRoomPrice.setError("Giá phòng là bắt buộc");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(roomPriceStr);
            if (price < 100000 || price > 1000000) {
                etRoomPrice.setError("Giá phòng phải từ 100,000 đến 1,000,000");
                return;
            }
        } catch (NumberFormatException e) {
            etRoomPrice.setError("Giá phòng phải là một con số");
            return;
        }
        // --- End Validation ---

        // Using an executor to run the database check off the main thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Room existingRoom = roomViewModel.getRoomByNumberSync(roomNumberStr);
            boolean isDuplicate = existingRoom != null && existingRoom.getId() != currentRoomId;

            runOnUiThread(() -> {
                if (isDuplicate) {
                    etRoomNumber.setError("Số phòng này đã tồn tại");
                } else {
                    // Proceed with saving
                    if (currentRoomId != -1) {
                        Room updatedRoom = new Room(roomNumberStr, roomTypeStr, price);
                        updatedRoom.setId(currentRoomId);
                        if (currentRoom != null) {
                            updatedRoom.setStatus(currentRoom.getStatus());
                        }
                        roomViewModel.update(updatedRoom);
                        Toast.makeText(this, "Phòng đã được cập nhật", Toast.LENGTH_SHORT).show();
                    } else {
                        Room newRoom = new Room(roomNumberStr, roomTypeStr, price);
                        roomViewModel.insert(newRoom);
                        Toast.makeText(this, "Phòng đã được lưu", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            });
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa phòng này không?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteRoom())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteRoom() {
        if (currentRoom != null) {
            roomViewModel.delete(currentRoom);
            Toast.makeText(this, "Phòng đã được xóa", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
