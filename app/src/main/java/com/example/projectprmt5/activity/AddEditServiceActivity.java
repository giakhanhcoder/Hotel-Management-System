package com.example.projectprmt5.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.R;
import com.example.projectprmt5.database.entities.Service;
import com.example.projectprmt5.viewmodel.ServiceViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditServiceActivity extends AppCompatActivity {

    public static final String EXTRA_SERVICE = "com.example.projectprmt5.EXTRA_SERVICE";

    private TextInputEditText editTextName, editTextDescription, editTextPrice;
    private ServiceViewModel serviceViewModel;
    private Service currentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_service);

        editTextName = findViewById(R.id.edit_text_service_name);
        editTextDescription = findViewById(R.id.edit_text_service_description);
        editTextPrice = findViewById(R.id.edit_text_service_price);
        Button saveButton = findViewById(R.id.button_save_service);

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_service);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serviceViewModel = new ViewModelProvider(this).get(ServiceViewModel.class);

        if (getIntent().hasExtra(EXTRA_SERVICE)) {
            currentService = getIntent().getParcelableExtra(EXTRA_SERVICE);
            getSupportActionBar().setTitle("Edit Service");
            editTextName.setText(currentService.getName());
            editTextDescription.setText(currentService.getDescription());
            editTextPrice.setText(String.valueOf(currentService.getPrice()));
        } else {
            getSupportActionBar().setTitle("Add New Service");
        }

        saveButton.setOnClickListener(v -> saveService());
    }

    private void saveService() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentService != null) {
            // Update existing service
            currentService.setName(name);
            currentService.setDescription(description);
            currentService.setPrice(price);
            serviceViewModel.update(currentService);
            Toast.makeText(this, "Service updated", Toast.LENGTH_SHORT).show();
        } else {
            // Insert new service
            Service newService = new Service(name, description, price);
            serviceViewModel.insert(newService);
            Toast.makeText(this, "Service saved", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
