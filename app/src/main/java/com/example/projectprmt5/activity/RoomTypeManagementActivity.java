package com.example.projectprmt5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.R;
import com.example.projectprmt5.database.entities.RoomType;
import com.example.projectprmt5.viewmodel.RoomTypeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RoomTypeManagementActivity extends AppCompatActivity {

    private RoomTypeViewModel roomTypeViewModel;
    private RoomTypeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_type_management);

        Toolbar toolbar = findViewById(R.id.toolbar_room_types);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Room Types");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_room_types);
        adapter = new RoomTypeAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        roomTypeViewModel = new ViewModelProvider(this).get(RoomTypeViewModel.class);
        roomTypeViewModel.getAllRoomTypes().observe(this, roomTypes -> {
            adapter.setRoomTypes(roomTypes);
        });

        FloatingActionButton fab = findViewById(R.id.fab_add_room_type);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(RoomTypeManagementActivity.this, AddEditRoomTypeActivity.class);
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

    // --- RecyclerView Adapter ---
    private class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.RoomTypeViewHolder> {

        private List<RoomType> roomTypes = new ArrayList<>();

        @NonNull
        @Override
        public RoomTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_type_item, parent, false);
            return new RoomTypeViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomTypeViewHolder holder, int position) {
            RoomType currentType = roomTypes.get(position);
            holder.textViewName.setText(currentType.getName());

            holder.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(RoomTypeManagementActivity.this, AddEditRoomTypeActivity.class);
                intent.putExtra(AddEditRoomTypeActivity.EXTRA_ID, currentType.getId());
                intent.putExtra(AddEditRoomTypeActivity.EXTRA_NAME, currentType.getName());
                startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(RoomTypeManagementActivity.this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete '" + currentType.getName() + "'?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> roomTypeViewModel.delete(currentType))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return roomTypes.size();
        }

        void setRoomTypes(List<RoomType> roomTypes) {
            this.roomTypes = roomTypes;
            notifyDataSetChanged();
        }

        class RoomTypeViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewName;
            private final Button btnEdit, btnDelete;

            public RoomTypeViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.item_room_type_name);
                btnEdit = itemView.findViewById(R.id.btn_edit_type);
                btnDelete = itemView.findViewById(R.id.btn_delete_type);
            }
        }
    }
}
