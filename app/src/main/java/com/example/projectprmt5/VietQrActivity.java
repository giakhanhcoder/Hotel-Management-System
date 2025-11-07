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
import com.example.projectprmt5.viewmodel.BookingViewModel;
import com.example.projectprmt5.viewmodel.PaymentViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class VietQrActivity extends AppCompatActivity {

    private PaymentViewModel paymentViewModel;
    private BookingViewModel bookingViewModel;
    private int bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viet_qr);

        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        // Find views
        WebView webView = findViewById(R.id.webViewQrCode);
        Button buttonConfirmPayment = findViewById(R.id.buttonConfirmPayment);
        TextView textViewAmountDetails = findViewById(R.id.textViewAmountDetails);
        TextView textViewAccountName = findViewById(R.id.textViewAccountName);
        TextView textViewAccountNumber = findViewById(R.id.textViewAccountNumber);
        TextView textViewOrderInfo = findViewById(R.id.textViewOrderInfo);

        Intent intent = getIntent();
        bookingId = intent.getIntExtra(PaymentActivity.EXTRA_BOOKING_ID, -1);
        double amount = intent.getDoubleExtra(PaymentActivity.EXTRA_AMOUNT, 0.0);

        if (bookingId != -1 && amount > 0) {
            String orderInfo = "Thanh toan don hang " + bookingId;

            // Load QR Code image
            String qrImageUrl = VietQRConfig.getQrImageUrl(amount, orderInfo);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.loadUrl(qrImageUrl);

            // Populate payment details
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            textViewAmountDetails.setText("Số tiền: " + currencyFormatter.format(amount));
            textViewAccountName.setText("Tên tài khoản: " + VietQRConfig.getAccountName());
            textViewAccountNumber.setText("Số tài khoản: " + VietQRConfig.getAccountNo() + " (" + VietQRConfig.getBankId() + ")");
            textViewOrderInfo.setText("Nội dung: " + orderInfo);

        } else {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonConfirmPayment.setOnClickListener(v -> {
            if (bookingId != -1) {
                // Update payment and booking status
                paymentViewModel.updatePaymentStatusByBookingId(bookingId, "SUCCESS");
                bookingViewModel.updateBookingStatus(bookingId, Booking.BookingStatus.CHECKED_OUT);

                Toast.makeText(this, "Đã xác nhận thanh toán! Cảm ơn bạn.", Toast.LENGTH_LONG).show();

                // Go back to Main Activity
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
