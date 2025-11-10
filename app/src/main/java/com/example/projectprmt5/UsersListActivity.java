package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectprmt5.adapter.UserAdapter;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * UsersListActivity
 * Display list of users filtered by role (RECEPTIONIST or CUSTOMER)
 */
public class UsersListActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;

    private UserRepository userRepository;
    private String userType; // "RECEPTIONIST" or "CUSTOMER"

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        // Get user type from intent
        userType = getIntent().getStringExtra("USER_TYPE");
        if (userType == null) {
            userType = "GUEST";
        }

        setupToolbar();
        initViews();
        initRepository();
        setupRecyclerView();
        setupListeners();
        loadUsers();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String title = userType.equals("RECEPTIONIST") ?
                "Danh sách Nhân viên" : "Danh sách Khách hàng";
            getSupportActionBar().setTitle(title);
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewUsers);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void initRepository() {
        userRepository = new UserRepository(getApplication());
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditUserActivity.class);
            intent.putExtra("USER_TYPE", userType);
            startActivity(intent);
        });
    }

    private void loadUsers() {
        userRepository.getUsersByRole(userType).observe(this, users -> {
            if (users != null && !users.isEmpty()) {
                adapter.updateUsers(users);
                recyclerView.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                String emptyText = userType.equals("RECEPTIONIST") ?
                    "Chưa có nhân viên nào" : "Chưa có khách hàng nào";
                tvEmpty.setText(emptyText);
            }
        });
    }

    @Override
    public void onUserClick(User user) {
        // Edit user
        Intent intent = new Intent(this, AddEditUserActivity.class);
        intent.putExtra("USER_ID", user.getUserId());
        intent.putExtra("USER_TYPE", userType);
        startActivity(intent);
    }

    @Override
    public void onUserLongClick(User user) {
        // Show delete dialog
        new AlertDialog.Builder(this)
            .setTitle("Xóa người dùng")
            .setMessage("Bạn có chắc muốn xóa " + user.getFullName() + "?")
            .setPositiveButton("Xóa", (dialog, which) -> deleteUser(user))
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

