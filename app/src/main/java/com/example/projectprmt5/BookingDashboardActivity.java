package com.example.projectprmt5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.viewmodel.BookingViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookingDashboardActivity extends AppCompatActivity {

    private BookingViewModel bookingViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("My Bookings");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_bookings);
        final BookingAdapter adapter = new BookingAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab_add_booking);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(BookingDashboardActivity.this, AddBookingActivity.class);
            startActivity(intent);
        });

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        // Use submitList with ListAdapter
        bookingViewModel.getAllBookings().observe(this, adapter::submitList);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- ADAPTER using ListAdapter for automatic diffing ---
    private static class BookingAdapter extends ListAdapter<Booking, BookingAdapter.BookingViewHolder> {

        protected BookingAdapter() {
            super(DIFF_CALLBACK);
        }

        @NonNull
        @Override
        public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.booking_item, parent, false);
            return new BookingViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
            Booking current = getItem(position);
            holder.bind(current);
        }

        class BookingViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewCode, textViewStatus, textViewDates, textViewRoomInfo;

            private BookingViewHolder(View itemView) {
                super(itemView);
                textViewCode = itemView.findViewById(R.id.item_booking_code);
                textViewStatus = itemView.findViewById(R.id.item_booking_status);
                textViewDates = itemView.findViewById(R.id.item_booking_dates);
                textViewRoomInfo = itemView.findViewById(R.id.item_booking_room_info);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Booking clickedBooking = getItem(position);
                        Intent intent = new Intent(v.getContext(), BookingDetailActivity.class);
                        intent.putExtra(BookingDetailActivity.EXTRA_BOOKING_ID, clickedBooking.getBookingId());
                        v.getContext().startActivity(intent);
                    }
                });
            }

            public void bind(Booking booking) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                textViewCode.setText("Code: " + booking.getBookingCode());
                textViewStatus.setText(booking.getStatus());
                textViewDates.setText("From: " + dateFormat.format(booking.getCheckInDate()) + " To: " + dateFormat.format(booking.getCheckOutDate()));
                textViewRoomInfo.setText("Room: " + booking.getRoomId() + " | Guests: " + booking.getNumberOfGuests());

                int statusColor = getStatusColor(booking.getStatus());
                textViewStatus.getBackground().setTint(statusColor);
            }

            private int getStatusColor(String status) {
                // Simplified switch for brevity
                switch (status) {
                    case "CONFIRMED": return ContextCompat.getColor(itemView.getContext(), R.color.status_confirmed);
                    case "CHECKED_IN": return ContextCompat.getColor(itemView.getContext(), R.color.status_checked_in);
                    case "PENDING": return ContextCompat.getColor(itemView.getContext(), R.color.status_pending);
                    case "CHECKED_OUT": return ContextCompat.getColor(itemView.getContext(), R.color.status_checked_out);
                    case "CANCELLED": return ContextCompat.getColor(itemView.getContext(), R.color.status_cancelled);
                    default: return Color.GRAY;
                }
            }
        }

        private static final DiffUtil.ItemCallback<Booking> DIFF_CALLBACK = new DiffUtil.ItemCallback<Booking>() {
            @Override
            public boolean areItemsTheSame(@NonNull Booking oldItem, @NonNull Booking newItem) {
                return oldItem.getBookingId() == newItem.getBookingId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Booking oldItem, @NonNull Booking newItem) {
                return oldItem.getStatus().equals(newItem.getStatus()) &&
                       oldItem.getCheckInDate().equals(newItem.getCheckInDate()) &&
                       oldItem.getCheckOutDate().equals(newItem.getCheckOutDate());
            }
        };
    }
}
