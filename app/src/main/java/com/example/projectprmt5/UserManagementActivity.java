package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;

/**
 * UserManagementActivity
 * Hub screen for managing different types of users (Staff and Customers)
 */
public class UserManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        setupToolbar();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý người dùng");
        }
    }

    private void setupListeners() {
        // Staff Management Card
        MaterialCardView cardStaff = findViewById(R.id.cardStaffManagement);
        cardStaff.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsersListActivity.class);
            intent.putExtra("USER_TYPE", "RECEPTIONIST");
            startActivity(intent);
        });

        // Customer Management Card
        MaterialCardView cardCustomer = findViewById(R.id.cardCustomerManagement);
        cardCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsersListActivity.class);
            intent.putExtra("USER_TYPE", "GUEST");
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

