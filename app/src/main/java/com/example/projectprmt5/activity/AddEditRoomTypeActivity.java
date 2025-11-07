package com.example.projectprmt5.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.R;
import com.example.projectprmt5.database.entities.RoomType;
import com.example.projectprmt5.viewmodel.RoomTypeViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditRoomTypeActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.projectprmt5.EXTRA_ID";
    public static final String EXTRA_NAME = "com.example.projectprmt5.EXTRA_NAME";

    private TextInputEditText etRoomTypeName;
    private Button btnSave;

    private RoomTypeViewModel roomTypeViewModel;
    private int currentTypeId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room_type);

        etRoomTypeName = findViewById(R.id.et_room_type_name);
        btnSave = findViewById(R.id.btn_save_room_type);

        roomTypeViewModel = new ViewModelProvider(this).get(RoomTypeViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_ID)) {
            setTitle("Edit Room Type");
            currentTypeId = extras.getInt(EXTRA_ID);
            etRoomTypeName.setText(extras.getString(EXTRA_NAME));
        } else {
            setTitle("Add New Room Type");
        }

        btnSave.setOnClickListener(v -> saveRoomType());
    }

    private void saveRoomType() {
        String name = etRoomTypeName.getText().toString().trim().toUpperCase();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter a room type name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentTypeId != -1) {
            RoomType roomType = new RoomType(name);
            roomType.setId(currentTypeId);
            roomTypeViewModel.update(roomType);
            Toast.makeText(this, "Room type updated", Toast.LENGTH_SHORT).show();
        } else {
            RoomType roomType = new RoomType(name);
            roomTypeViewModel.insert(roomType);
            Toast.makeText(this, "Room type saved", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
