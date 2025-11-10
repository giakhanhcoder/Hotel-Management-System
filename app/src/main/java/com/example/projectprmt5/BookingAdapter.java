package com.example.projectprmt5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Room;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookings;
    private OnBookingClickListener listener;
    private SimpleDateFormat dateFormatter;
    private NumberFormat currencyFormatter;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    public BookingAdapter(List<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings != null ? bookings : new ArrayList<>();
        this.listener = listener;
        this.dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.currencyFormatter = NumberFormat.getNumberInstance(new Locale("vi"));
    }

    public void updateBookings(List<Booking> newBookings) {
        this.bookings = newBookings != null ? newBookings : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_card, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBookingCode;
        private TextView tvBookingStatus;
        private TextView tvRoomInfo;
        private TextView tvCheckInDate;
        private TextView tvCheckOutDate;
        private TextView tvTotalAmount;
        private MaterialCardView cardView;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingCode = itemView.findViewById(R.id.tvBookingCode);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvRoomInfo = itemView.findViewById(R.id.tvRoomInfo);
            tvCheckInDate = itemView.findViewById(R.id.tvCheckInDate);
            tvCheckOutDate = itemView.findViewById(R.id.tvCheckOutDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            cardView = (MaterialCardView) itemView;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBookingClick(bookings.get(position));
                }
            });
        }

        void bind(Booking booking) {
            // Booking Code
            tvBookingCode.setText(booking.getBookingCode() != null ? booking.getBookingCode() : "");

            // Status
            String status = booking.getStatus();
            tvBookingStatus.setText(getStatusText(status));
            setStatusBackground(status);

            // Room Info - Load from database asynchronously if needed
            tvRoomInfo.setText("Phòng " + booking.getRoomId());

            // Dates
            if (booking.getCheckInDate() != null) {
                tvCheckInDate.setText("Nhận: " + dateFormatter.format(booking.getCheckInDate()));
            } else {
                tvCheckInDate.setText("Nhận: --");
            }

            if (booking.getCheckOutDate() != null) {
                tvCheckOutDate.setText("Trả: " + dateFormatter.format(booking.getCheckOutDate()));
            } else {
                tvCheckOutDate.setText("Trả: --");
            }

            // Total Amount
            tvTotalAmount.setText(formatCurrency(booking.getTotalAmount()));
        }

        private String getStatusText(String status) {
            if (status == null) return "";
            switch (status) {
                case Booking.BookingStatus.PENDING:
                    return "Chờ xác nhận";
                case Booking.BookingStatus.CONFIRMED:
                    return "Đã xác nhận";
                case Booking.BookingStatus.CHECKED_IN:
                    return "Đã nhận phòng";
                case Booking.BookingStatus.CHECKED_OUT:
                    return "Đã trả phòng";
                case Booking.BookingStatus.CANCELLED:
                    return "Đã hủy";
                default:
                    return status;
            }
        }

        private void setStatusBackground(String status) {
            if (status == null) return;

            int backgroundColor;
            switch (status) {
                case Booking.BookingStatus.PENDING:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.booking_pending);
                    break;
                case Booking.BookingStatus.CONFIRMED:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.booking_confirmed);
                    break;
                case Booking.BookingStatus.CHECKED_IN:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.room_occupied);
                    break;
                case Booking.BookingStatus.CHECKED_OUT:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.text_secondary);
                    break;
                case Booking.BookingStatus.CANCELLED:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.payment_failed);
                    break;
                default:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.text_secondary);
                    break;
            }

            tvBookingStatus.setBackgroundColor(backgroundColor);
        }

        private String formatCurrency(double amount) {
            return currencyFormatter.format(amount) + " VND";
        }
    }
}

