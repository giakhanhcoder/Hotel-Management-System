package com.example.projectprmt5.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
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

    private ImageView ivRoomImage;
    private TextInputEditText etRoomNumber;
    private Spinner spinnerRoomType;
    private TextInputEditText etRoomPrice;
    private Button btnDelete, btnSelectImage;

    private RoomViewModel roomViewModel;
    private RoomTypeViewModel roomTypeViewModel;
    private int currentRoomId = -1;
    private Room currentRoom;
    private List<String> roomTypeNames = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room);

        initViews();
        initImagePickerLauncher();
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
                    if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                        selectedImageUri = Uri.parse(room.getImageUrl());
                        Glide.with(this).load(selectedImageUri).placeholder(R.drawable.ic_image_placeholder).into(ivRoomImage);
                    }
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
        btnSelectImage.setOnClickListener(v -> showImageSourceDialog());
    }

    private void initViews() {
        ivRoomImage = findViewById(R.id.iv_room_image);
        etRoomNumber = findViewById(R.id.et_room_number);
        spinnerRoomType = findViewById(R.id.spinner_room_type);
        etRoomPrice = findViewById(R.id.et_room_price);
        btnDelete = findViewById(R.id.btn_delete_room);
        btnSelectImage = findViewById(R.id.btn_select_image);
    }

    private void initImagePickerLauncher() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                selectedImageUri = result.getData().getData();
                Glide.with(this).load(selectedImageUri).into(ivRoomImage);
                final int takeFlags = result.getData().getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showImageSourceDialog() {
        final CharSequence[] options = {"Select from Gallery", "Use Image URL", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Room Image");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Select from Gallery")) {
                pickImageFromGallery();
            } else if (options[item].equals("Use Image URL")) {
                showImageUrlDialog();
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void showImageUrlDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Image URL");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String url = input.getText().toString();
            if (!TextUtils.isEmpty(url)) {
                selectedImageUri = Uri.parse(url);
                Glide.with(AddEditRoomActivity.this)
                     .load(selectedImageUri)
                     .placeholder(R.drawable.ic_image_placeholder)
                     .into(ivRoomImage);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
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

        if (TextUtils.isEmpty(roomNumberStr) || roomTypeStr == null || TextUtils.isEmpty(roomPriceStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
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
                    Room roomToSave;
                    if (currentRoomId != -1) {
                        roomToSave = currentRoom;
                    } else {
                        roomToSave = new Room(roomNumberStr, roomTypeStr, price);
                    }
                    roomToSave.setRoomNumber(roomNumberStr);
                    roomToSave.setType(roomTypeStr);
                    roomToSave.setPrice(price);
                    roomToSave.setImageUrl(selectedImageUri.toString());
                    
                    if (currentRoomId != -1) {
                        roomViewModel.update(roomToSave);
                        Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show();
                    } else {
                        roomViewModel.insert(roomToSave);
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
