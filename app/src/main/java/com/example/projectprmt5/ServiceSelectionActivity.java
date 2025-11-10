package com.example.projectprmt5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.database.entities.Service;
import com.example.projectprmt5.viewmodel.ServiceViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ServiceSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_CURRENT_SERVICES = "com.example.projectprmt5.EXTRA_CURRENT_SERVICES";
    public static final String EXTRA_SELECTED_SERVICES = "com.example.projectprmt5.EXTRA_SELECTED_SERVICES";

    private ServiceViewModel serviceViewModel;
    private ServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_selection);

        ArrayList<Service> currentServices = getIntent().getParcelableArrayListExtra(EXTRA_CURRENT_SERVICES);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_services);
        adapter = new ServiceAdapter(currentServices);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        serviceViewModel = new ViewModelProvider(this).get(ServiceViewModel.class);
        serviceViewModel.getAllServices().observe(this, services -> {
            adapter.setAllServices(services);
        });

        Button saveButton = findViewById(R.id.button_save_services);
        saveButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra(EXTRA_SELECTED_SERVICES, new ArrayList<>(adapter.getSelectedServices()));
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    // --- ADAPTER --- 
    private static class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
        private List<Service> allServices = new ArrayList<>();
        private final Set<Service> selectedServices;

        ServiceAdapter(ArrayList<Service> currentServices) {
            this.selectedServices = new HashSet<>();
            if (currentServices != null) {
                this.selectedServices.addAll(currentServices);
            }
        }

        void setAllServices(List<Service> services) {
            this.allServices = services;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_service, parent, false);
            return new ServiceViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
            Service currentService = allServices.get(position);
            holder.bind(currentService);

            holder.checkBox.setOnCheckedChangeListener(null); 
            holder.checkBox.setChecked(selectedServices.contains(currentService));
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedServices.add(currentService);
                } else {
                    selectedServices.remove(currentService);
                }
            });
        }

        @Override
        public int getItemCount() {
            return allServices.size();
        }

        Set<Service> getSelectedServices() {
            return selectedServices;
        }

        static class ServiceViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewName, textViewDescription, textViewPrice;
            private final CheckBox checkBox;

            ServiceViewHolder(View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.text_view_service_name);
                textViewDescription = itemView.findViewById(R.id.text_view_service_description);
                textViewPrice = itemView.findViewById(R.id.text_view_service_price);
                checkBox = itemView.findViewById(R.id.checkbox_service);
            }

            void bind(Service service) {
                textViewName.setText(service.getName());
                textViewDescription.setText(service.getDescription());
                textViewPrice.setText(String.format(Locale.getDefault(), "$%.2f", service.getPrice()));
            }
        }
    }
}
