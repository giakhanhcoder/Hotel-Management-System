package com.example.projectprmt5;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.repository.RoomRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * RoomManagementActivity
 * Quản lý thông tin phòng
 */
public class RoomManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddRoom;
    private RoomRepository roomRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_management);

        setupToolbar();
        initViews();
        initRepository();
        loadRooms();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Room");
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddRoom = findViewById(R.id.fabAddRoom);
        fabAddRoom.setOnClickListener(v -> {
            // TODO: Implement add room functionality
            Toast.makeText(this, "Thêm phòng mới", Toast.LENGTH_SHORT).show();
        });
    }

    private void initRepository() {
        roomRepository = new RoomRepository(getApplication());
    }

    private void loadRooms() {
        roomRepository.getAllRooms().observe(this, rooms -> {
            if (rooms != null) {
                // TODO: Set up adapter with rooms data
                Toast.makeText(this, "Tải " + rooms.size() + " phòng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

