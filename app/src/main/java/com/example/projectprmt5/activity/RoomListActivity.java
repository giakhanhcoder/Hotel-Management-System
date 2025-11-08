package com.example.projectprmt5.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectprmt5.AddBookingActivity;
import com.example.projectprmt5.R;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.viewmodel.RoomViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomListActivity extends AppCompatActivity {

    private RoomViewModel roomViewModel;
    private RoomAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        Toolbar toolbar = findViewById(R.id.toolbar_room_list);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Available Rooms");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_rooms);
        adapter = new RoomAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        roomViewModel.getAllRooms().observe(this, rooms -> {
            adapter.setRooms(rooms);
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

    private class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

        private List<Room> rooms = new ArrayList<>();

        @NonNull
        @Override
        public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_item, parent, false);
            return new RoomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
            Room currentRoom = rooms.get(position);
            holder.bind(currentRoom);

            holder.itemView.setOnClickListener(v -> {
                if (currentRoom.getStatus().equals(Room.RoomStatus.AVAILABLE)) {
                    Intent intent = new Intent(RoomListActivity.this, AddBookingActivity.class);
                    intent.putExtra("SELECTED_ROOM_ID", currentRoom.getId());
                    intent.putExtra("SELECTED_ROOM_NUMBER", currentRoom.getRoomNumber());
                    startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "This room is not available for booking.", Toast.LENGTH_SHORT).show();
                }
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

        class RoomViewHolder extends RecyclerView.ViewHolder {
            private final ImageView roomImage;
            private final TextView roomNumber, roomType, roomPrice, roomStatus;

            public RoomViewHolder(@NonNull View itemView) {
                super(itemView);
                roomImage = itemView.findViewById(R.id.item_room_image);
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

                if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                    Glide.with(itemView.getContext()).load(Uri.parse(room.getImageUrl())).into(roomImage);
                } else {
                    roomImage.setImageResource(R.drawable.ic_image_placeholder);
                }

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
