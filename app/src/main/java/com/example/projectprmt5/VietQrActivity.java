package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.viewmodel.BookingViewModel;
import com.example.projectprmt5.viewmodel.PaymentViewModel;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class VietQrActivity extends AppCompatActivity {

    private PaymentViewModel paymentViewModel;
    private BookingViewModel bookingViewModel;
    private int bookingId;
    private long paymentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viet_qr);

        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        WebView webView = findViewById(R.id.webViewQrCode);
        Button buttonConfirmPayment = findViewById(R.id.buttonConfirmPayment);
        TextView textViewAmountDetails = findViewById(R.id.textViewAmountDetails);
        TextView textViewAccountName = findViewById(R.id.textViewAccountName);
        TextView textViewAccountNumber = findViewById(R.id.textViewAccountNumber);
        TextView textViewOrderInfo = findViewById(R.id.textViewOrderInfo);

        Intent intent = getIntent();
        bookingId = intent.getIntExtra(PaymentActivity.EXTRA_BOOKING_ID, -1);
        double amount = intent.getDoubleExtra(PaymentActivity.EXTRA_AMOUNT, 0.0);
        // --- FIX: Use the correct key to retrieve the paymentId ---
        paymentId = intent.getLongExtra(PaymentActivity.EXTRA_PAYMENT_ID, -1);

        if (bookingId != -1 && amount > 0 && paymentId != -1) {
            String orderInfo = "Thanh toan don hang " + bookingId;

            String qrImageUrl = VietQRConfig.getQrImageUrl(amount, orderInfo);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.loadUrl(qrImageUrl);

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            textViewAmountDetails.setText("Số tiền: " + currencyFormatter.format(amount));
            textViewAccountName.setText("Tên tài khoản: " + VietQRConfig.getAccountName());
            textViewAccountNumber.setText("Số tài khoản: " + VietQRConfig.getAccountNo() + " (" + VietQRConfig.getBankId() + ")");
            textViewOrderInfo.setText("Nội dung: " + orderInfo);

        } else {
            Toast.makeText(this, "Dữ liệu không hợp lệ hoặc thiếu Payment ID", Toast.LENGTH_LONG).show();
            finish();
        }

        buttonConfirmPayment.setOnClickListener(v -> {
            if (paymentId != -1 && bookingId != -1) {
                // Use a new thread to prevent deadlocks
                new Thread(() -> {
                    try {
                        // Wait for the database updates to complete
                        paymentViewModel.updatePaymentStatus((int) paymentId, Payment.PaymentStatus.SUCCESS).get();
                        bookingViewModel.updateBookingStatus(bookingId, Booking.BookingStatus.CHECKED_OUT).get();

                        // Switch back to the main thread to show UI
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Đã xác nhận thanh toán! Cảm ơn bạn.", Toast.LENGTH_LONG).show();

                            Intent invoiceIntent = new Intent(this, InvoiceActivity.class);
                            invoiceIntent.putExtra("PAYMENT_ID", paymentId);
                            startActivity(invoiceIntent);
                            finish();
                        });

                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(this, "Lỗi khi xác nhận thanh toán", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }
}
