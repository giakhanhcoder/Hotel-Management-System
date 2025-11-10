package com.example.projectprmt5;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.repository.ServiceRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * ServiceManagementActivity
 * Quản lý dịch vụ khách sạn
 */
public class ServiceManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddService;
    private ServiceRepository serviceRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_management);

        setupToolbar();
        initViews();
        initRepository();
        loadServices();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Service");
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddService = findViewById(R.id.fabAddService);
        fabAddService.setOnClickListener(v -> {
            // TODO: Implement add service functionality
            Toast.makeText(this, "Thêm dịch vụ mới", Toast.LENGTH_SHORT).show();
        });
    }

    private void initRepository() {
        serviceRepository = new ServiceRepository(getApplication());
    }

    private void loadServices() {
        serviceRepository.getAllServices().observe(this, services -> {
            if (services != null) {
                // TODO: Set up adapter with services data
                Toast.makeText(this, "Tải " + services.size() + " dịch vụ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

