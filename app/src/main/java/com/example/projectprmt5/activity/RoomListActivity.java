package com.example.projectprmt5.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.R;
import com.example.projectprmt5.adapter.RoomListAdapter;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.viewmodel.RoomViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RoomListActivity extends AppCompatActivity {

    private RoomViewModel roomViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRooms);
        final RoomListAdapter adapter = new RoomListAdapter(new RoomListAdapter.RoomDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        roomViewModel.getAllRooms().observe(this, rooms -> {
            adapter.submitList(rooms);
        });

        FloatingActionButton fab = findViewById(R.id.fabAddRoom);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(RoomListActivity.this, AddEditRoomActivity.class);
            startActivity(intent);
        });

        adapter.setOnItemClickListener(room -> {
            Intent intent = new Intent(RoomListActivity.this, AddEditRoomActivity.class);
            intent.putExtra(AddEditRoomActivity.EXTRA_ID, room.getId());
            startActivity(intent);
        });
    }
}
