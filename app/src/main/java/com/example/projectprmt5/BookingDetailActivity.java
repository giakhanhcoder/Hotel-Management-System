package com.example.projectprmt5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Service;
import com.example.projectprmt5.viewmodel.BookingViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "com.example.projectprmt5.EXTRA_BOOKING_ID";

    private BookingViewModel bookingViewModel;
    private Booking currentBooking;
    private int bookingId;

    private TextView textViewBookingCode, textViewBookingStatus, textViewRoomId, textViewGuestId,
            textViewCheckInDate, textViewCheckOutDate, textViewNumGuests, textViewTotalAmount, textViewServicesTotal;
    private Button btnCheckIn, btnCancel, btnEdit, btnDelete, btnCheckoutAndBilling, btnAddServices;
    private RecyclerView recyclerViewSelectedServices;
    private SelectedServicesAdapter servicesAdapter;

    private ArrayList<Service> selectedServices = new ArrayList<>();
    private ActivityResultLauncher<Intent> serviceSelectionLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Booking Details");
        }

        initViews();

        serviceSelectionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<Service> returnedServices = result.getData().getParcelableArrayListExtra(ServiceSelectionActivity.EXTRA_SELECTED_SERVICES);
                    if (returnedServices != null) {
                        selectedServices = returnedServices;
                        updateServicesUI();
                    }
                }
            });

        bookingId = getIntent().getIntExtra(EXTRA_BOOKING_ID, -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Error: Invalid Booking ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        bookingViewModel.getBookingById(bookingId).observe(this, booking -> {
            if (booking != null) {
                currentBooking = booking;
                updateBookingUI(currentBooking);
                updateServicesUI();
            }
        });

        setupButtonClickListeners();
    }

    private void initViews() {
        textViewBookingCode = findViewById(R.id.text_view_booking_code);
        textViewBookingStatus = findViewById(R.id.text_view_booking_status);
        textViewRoomId = findViewById(R.id.text_view_room_id);
        textViewGuestId = findViewById(R.id.text_view_guest_id);
        textViewCheckInDate = findViewById(R.id.text_view_check_in_date);
        textViewCheckOutDate = findViewById(R.id.text_view_check_out_date);
        textViewNumGuests = findViewById(R.id.text_view_number_of_guests);
        textViewTotalAmount = findViewById(R.id.text_view_total_amount);
        textViewServicesTotal = findViewById(R.id.text_view_services_total);

        recyclerViewSelectedServices = findViewById(R.id.recycler_view_selected_services);
        servicesAdapter = new SelectedServicesAdapter();
        recyclerViewSelectedServices.setAdapter(servicesAdapter);
        recyclerViewSelectedServices.setLayoutManager(new LinearLayoutManager(this));

        btnAddServices = findViewById(R.id.btn_add_services);
        btnCheckIn = findViewById(R.id.btn_check_in);
        btnCheckoutAndBilling = findViewById(R.id.btn_checkout_and_billing);
        btnCancel = findViewById(R.id.btn_cancel_booking);
        btnEdit = findViewById(R.id.btn_edit_booking);
        btnDelete = findViewById(R.id.btn_delete_booking);
    }

    private void setupButtonClickListeners() {
        btnAddServices.setOnClickListener(v -> {
            if (Booking.BookingStatus.CHECKED_OUT.equals(currentBooking.getStatus())) {
                Toast.makeText(this, "Cannot add services to a checked-out booking.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ServiceSelectionActivity.class);
            intent.putParcelableArrayListExtra(ServiceSelectionActivity.EXTRA_CURRENT_SERVICES, selectedServices);
            serviceSelectionLauncher.launch(intent);
        });

        btnCheckoutAndBilling.setOnClickListener(v -> {
            if (currentBooking != null) {
                if (Booking.BookingStatus.CHECKED_OUT.equals(currentBooking.getStatus())) {
                    Toast.makeText(this, "This booking has already been checked out.", Toast.LENGTH_SHORT).show();
                    return;
                }
                double roomTotal = currentBooking.getTotalAmount();
                double servicesTotal = calculateServicesTotal();
                double finalTotal = roomTotal + servicesTotal;

                Intent intent = new Intent(BookingDetailActivity.this, PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_BOOKING_ID, currentBooking.getBookingId());
                intent.putExtra(PaymentActivity.EXTRA_AMOUNT, finalTotal);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Booking details not loaded.", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCheckIn.setOnClickListener(v -> {
            if (currentBooking != null && Booking.BookingStatus.CHECKED_OUT.equals(currentBooking.getStatus())) {
                Toast.makeText(this, "Cannot check in a booking that is already checked out.", Toast.LENGTH_SHORT).show();
                return;
            }
            bookingViewModel.checkIn(bookingId);
            Toast.makeText(this, "Checked-In Successfully!", Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> {
            bookingViewModel.updateBookingStatus(bookingId, Booking.BookingStatus.CANCELLED);
            Toast.makeText(this, "Booking Cancelled!", Toast.LENGTH_SHORT).show();
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this booking?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (currentBooking != null) {
                        bookingViewModel.delete(currentBooking);
                        Toast.makeText(this, "Booking Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBookingActivity.class);
            intent.putExtra(AddBookingActivity.EXTRA_BOOKING_ID, bookingId);
            startActivity(intent);
        });
    }
    
    private void updateBookingUI(Booking booking) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        getSupportActionBar().setSubtitle("Code: " + booking.getBookingCode());
        textViewBookingStatus.setText("Status: " + booking.getStatus());
        textViewRoomId.setText("Room ID: " + booking.getRoomId());
        textViewGuestId.setText("Guest ID: " + booking.getGuestId());
        textViewCheckInDate.setText("Check-in: " + dateFormat.format(booking.getCheckInDate()));
        textViewCheckOutDate.setText("Check-out: " + dateFormat.format(booking.getCheckOutDate()));
        textViewNumGuests.setText("Guests: " + booking.getNumberOfGuests());

        // Disable buttons if booking is checked out
        if (Booking.BookingStatus.CHECKED_OUT.equals(booking.getStatus())) {
            btnCheckIn.setEnabled(false);
            btnCheckoutAndBilling.setEnabled(false);
            btnAddServices.setEnabled(false);
            btnCancel.setEnabled(false);
            btnEdit.setEnabled(false);
        } else {
            btnCheckIn.setEnabled(true);
            btnCheckoutAndBilling.setEnabled(true);
            btnAddServices.setEnabled(true);
            btnCancel.setEnabled(true);
            btnEdit.setEnabled(true);
        }
    }

    private void updateServicesUI() {
        servicesAdapter.setServices(selectedServices);
        double servicesTotal = calculateServicesTotal();
        textViewServicesTotal.setText(String.format(Locale.getDefault(), "Services Total: $%.2f", servicesTotal));

        double roomTotal = (currentBooking != null) ? currentBooking.getTotalAmount() : 0;
        double finalTotal = roomTotal + servicesTotal;
        textViewTotalAmount.setText(String.format(Locale.getDefault(), "Grand Total: $%.2f", finalTotal));
    }

    private double calculateServicesTotal() {
        double total = 0;
        for (Service service : selectedServices) {
            total += service.getPrice();
        }
        return total;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- ADAPTER for selected services --- 
    private static class SelectedServicesAdapter extends RecyclerView.Adapter<SelectedServicesAdapter.ServiceViewHolder> {
        private List<Service> services = new ArrayList<>();

        @NonNull
        @Override
        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ServiceViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
            holder.bind(services.get(position));
        }

        @Override
        public int getItemCount() {
            return services.size();
        }

        void setServices(List<Service> services) {
            this.services = services;
            notifyDataSetChanged();
        }

        class ServiceViewHolder extends RecyclerView.ViewHolder {
            private final TextView text1, text2;

            ServiceViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }

            void bind(Service service) {
                text1.setText(service.getName());
                text2.setText(String.format(Locale.getDefault(), "$%.2f", service.getPrice()));
            }
        }
    }
}
