package com.example.projectprmt5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.R;
import com.example.projectprmt5.database.entities.Service;
import com.example.projectprmt5.viewmodel.ServiceViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminServiceManagementActivity extends AppCompatActivity {

    private ServiceViewModel serviceViewModel;
    private AdminServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service_management);

        Toolbar toolbar = findViewById(R.id.toolbar_service_management);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_admin_services);
        adapter = new AdminServiceAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        serviceViewModel = new ViewModelProvider(this).get(ServiceViewModel.class);
        serviceViewModel.getAllServices().observe(this, services -> {
            adapter.setServices(services);
        });

        FloatingActionButton fab = findViewById(R.id.fab_add_service);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(AdminServiceManagementActivity.this, AddEditServiceActivity.class);
            startActivity(intent);
        });

        adapter.setListener(new AdminServiceAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Service service) {
                Intent intent = new Intent(AdminServiceManagementActivity.this, AddEditServiceActivity.class);
                intent.putExtra(AddEditServiceActivity.EXTRA_SERVICE, service);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Service service) {
                new AlertDialog.Builder(AdminServiceManagementActivity.this)
                        .setTitle("Delete Service")
                        .setMessage("Are you sure you want to delete '" + service.getName() + "'?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            serviceViewModel.delete(service);
                            Toast.makeText(AdminServiceManagementActivity.this, service.getName() + " deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // --- ADAPTER ---
    static class AdminServiceAdapter extends RecyclerView.Adapter<AdminServiceAdapter.ServiceViewHolder> {
        private List<Service> services = new ArrayList<>();
        private OnItemClickListener listener;

        @NonNull
        @Override
        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_service, parent, false);
            return new ServiceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
            Service currentService = services.get(position);
            holder.name.setText(currentService.getName());
            holder.description.setText(currentService.getDescription());
            holder.price.setText(String.format(Locale.getDefault(), "$%.2f", currentService.getPrice()));

            holder.editButton.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(currentService);
            });
            holder.deleteButton.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(currentService);
            });
        }

        @Override
        public int getItemCount() {
            return services.size();
        }

        public void setServices(List<Service> services) {
            this.services = services;
            notifyDataSetChanged();
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public interface OnItemClickListener {
            void onEditClick(Service service);
            void onDeleteClick(Service service);
        }

        static class ServiceViewHolder extends RecyclerView.ViewHolder {
            TextView name, description, price;
            Button editButton, deleteButton;

            public ServiceViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.text_view_admin_service_name);
                description = itemView.findViewById(R.id.text_view_admin_service_description);
                price = itemView.findViewById(R.id.text_view_admin_service_price);
                editButton = itemView.findViewById(R.id.button_edit_service);
                deleteButton = itemView.findViewById(R.id.button_delete_service);
            }
        }
    }
}
