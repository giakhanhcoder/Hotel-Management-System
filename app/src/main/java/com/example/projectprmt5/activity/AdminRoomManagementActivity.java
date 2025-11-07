package com.example.projectprmt5.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.R;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.viewmodel.RoomViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminRoomManagementActivity extends AppCompatActivity {

    private RoomViewModel roomViewModel;
    private AdminRoomAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room_management);

        Toolbar toolbar = findViewById(R.id.toolbar_admin_rooms);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Rooms");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button btnManageRoomTypes = findViewById(R.id.btn_manage_room_types);
        btnManageRoomTypes.setOnClickListener(v -> {
            startActivity(new Intent(AdminRoomManagementActivity.this, RoomTypeManagementActivity.class));
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_admin_rooms);
        adapter = new AdminRoomAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        roomViewModel.getAllRooms().observe(this, rooms -> {
            adapter.setRooms(rooms);
        });

        FloatingActionButton fab = findViewById(R.id.fab_add_room);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(AdminRoomManagementActivity.this, AddEditRoomActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- RecyclerView Adapter for Admin ---
    private class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.AdminRoomViewHolder> {

        private List<Room> rooms = new ArrayList<>();

        @NonNull
        @Override
        public AdminRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_item, parent, false);
            return new AdminRoomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AdminRoomViewHolder holder, int position) {
            Room currentRoom = rooms.get(position);
            holder.bind(currentRoom);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminRoomManagementActivity.this, AddEditRoomActivity.class);
                intent.putExtra(AddEditRoomActivity.EXTRA_ID, currentRoom.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return rooms.size();
        }

        void setRooms(List<Room> rooms) {
            this.rooms = rooms;
            notifyDataSetChanged();
        }

        class AdminRoomViewHolder extends RecyclerView.ViewHolder {
            private final TextView roomNumber, roomType, roomPrice, roomStatus;

            public AdminRoomViewHolder(@NonNull View itemView) {
                super(itemView);
                roomNumber = itemView.findViewById(R.id.item_room_number);
                roomType = itemView.findViewById(R.id.item_room_type);
                roomPrice = itemView.findViewById(R.id.item_room_price);
                roomStatus = itemView.findViewById(R.id.item_room_status);
            }

            public void bind(Room room) {
                roomNumber.setText("Room " + room.getRoomNumber());
                roomType.setText(room.getType());
                roomPrice.setText(String.format(Locale.getDefault(), "$%.2f / night", room.getPrice()));
                roomStatus.setText(room.getStatus());

                int color = getStatusColor(room.getStatus());
                roomStatus.getBackground().setTint(ContextCompat.getColor(itemView.getContext(), color));
            }

            private int getStatusColor(String status) {
                switch (status) {
                    case Room.RoomStatus.AVAILABLE:
                        return R.color.status_confirmed;
                    case Room.RoomStatus.RESERVED:
                        return R.color.status_pending;
                    case Room.RoomStatus.OCCUPIED:
                        return R.color.status_cancelled;
                    default:
                        return android.R.color.darker_gray;
                }
            }
        }
    }
}
