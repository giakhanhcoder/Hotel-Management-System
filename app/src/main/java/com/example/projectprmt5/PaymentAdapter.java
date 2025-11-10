package com.example.projectprmt5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.database.entities.Payment;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private List<Payment> payments;
    private OnPaymentClickListener listener;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat dateTimeFormatter;
    private NumberFormat currencyFormatter;

    public interface OnPaymentClickListener {
        void onPaymentClick(Payment payment);
    }

    public PaymentAdapter(List<Payment> payments, OnPaymentClickListener listener) {
        this.payments = payments != null ? payments : new ArrayList<>();
        this.listener = listener;
        this.dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.currencyFormatter = NumberFormat.getNumberInstance(new Locale("vi"));
    }

    public void updatePayments(List<Payment> newPayments) {
        this.payments = newPayments != null ? newPayments : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_card, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Payment payment = payments.get(position);
        holder.bind(payment);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTransactionId;
        private TextView tvPaymentStatus;
        private TextView tvAmount;
        private TextView tvPaymentMethod;
        private TextView tvPaymentDate;
        private TextView tvBookingCode;
        private MaterialCardView cardView;

        PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvPaymentDate = itemView.findViewById(R.id.tvPaymentDate);
            tvBookingCode = itemView.findViewById(R.id.tvBookingCode);
            cardView = (MaterialCardView) itemView;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPaymentClick(payments.get(position));
                }
            });
        }

        void bind(Payment payment) {
            // Transaction ID
            tvTransactionId.setText(payment.getTransactionId() != null ? payment.getTransactionId() : "");

            // Status
            String status = payment.getStatus();
            tvPaymentStatus.setText(getStatusText(status));
            setStatusBackground(status);

            // Amount
            tvAmount.setText(formatCurrency(payment.getAmount()));

            // Payment Method
            String method = payment.getPaymentMethod();
            if (method != null) {
                tvPaymentMethod.setText(getPaymentMethodText(method));
            } else {
                tvPaymentMethod.setText("");
            }

            // Payment Date
            if (payment.getPaymentDate() != null) {
                tvPaymentDate.setText(dateTimeFormatter.format(payment.getPaymentDate()));
            } else {
                tvPaymentDate.setText("");
            }

            // Booking Code - Will be loaded separately if needed
            if (payment.getBookingId() > 0) {
                tvBookingCode.setText("Booking ID: " + payment.getBookingId());
            } else {
                tvBookingCode.setText("");
            }
        }

        private String getStatusText(String status) {
            if (status == null) return "";
            switch (status) {
                case Payment.PaymentStatus.SUCCESS:
                    return "Thành công";
                case Payment.PaymentStatus.PENDING:
                    return "Chờ xử lý";
                case Payment.PaymentStatus.FAILED:
                    return "Thất bại";
                case Payment.PaymentStatus.REFUNDED:
                    return "Đã hoàn tiền";
                default:
                    return status;
            }
        }

        private String getPaymentMethodText(String method) {
            if (method == null) return "";
            switch (method.toUpperCase()) {
                case "VNPAY":
                    return "VNPAY";
                case "CASH":
                    return "Tiền mặt";
                case "CARD":
                    return "Thẻ";
                case "VIETQR":
                    return "VietQR";
                default:
                    return method;
            }
        }

        private void setStatusBackground(String status) {
            if (status == null) return;

            int backgroundColor;
            switch (status) {
                case Payment.PaymentStatus.SUCCESS:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.payment_success);
                    break;
                case Payment.PaymentStatus.PENDING:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.booking_pending);
                    break;
                case Payment.PaymentStatus.FAILED:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.payment_failed);
                    break;
                case Payment.PaymentStatus.REFUNDED:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.text_secondary);
                    break;
                default:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.text_secondary);
                    break;
            }

            tvPaymentStatus.setBackgroundColor(backgroundColor);
        }

        private String formatCurrency(double amount) {
            return currencyFormatter.format(amount) + " VND";
        }
    }
}

