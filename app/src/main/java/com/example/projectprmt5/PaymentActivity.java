package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.viewmodel.BookingViewModel;
import com.example.projectprmt5.viewmodel.PaymentViewModel;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "com.example.projectprmt5.EXTRA_BOOKING_ID";
    public static final String EXTRA_PAYMENT_ID = "com.example.projectprmt5.EXTRA_PAYMENT_ID";
    public static final String EXTRA_AMOUNT = "com.example.projectprmt5.EXTRA_AMOUNT";

    private TextView textViewBookingInfo, textViewAmount;
    private RadioGroup radioGroupPaymentMethods;
    private Button buttonProceedToPayment;

    private PaymentViewModel paymentViewModel;
    private BookingViewModel bookingViewModel;
    private int bookingId;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        textViewBookingInfo = findViewById(R.id.textViewBookingInfo);
        textViewAmount = findViewById(R.id.textViewAmount);
        radioGroupPaymentMethods = findViewById(R.id.radioGroupPaymentMethods);
        buttonProceedToPayment = findViewById(R.id.buttonProceedToPayment);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_BOOKING_ID) && intent.hasExtra(EXTRA_AMOUNT)) {
            bookingId = intent.getIntExtra(EXTRA_BOOKING_ID, -1);
            amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0.0);

            textViewBookingInfo.setText("Thanh toán cho Đơn đặt phòng #: " + bookingId);
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            textViewAmount.setText("Số tiền: " + currencyFormatter.format(amount));

        } else {
            Toast.makeText(this, "Thông tin thanh toán không hợp lệ", Toast.LENGTH_LONG).show();
            finish();
        }

        ((RadioButton) findViewById(R.id.radioButtonVnpay)).setChecked(true);

        buttonProceedToPayment.setOnClickListener(v -> handlePayment());
    }

    private void handlePayment() {
        int selectedId = radioGroupPaymentMethods.getCheckedRadioButtonId();

        if (bookingId == -1 || amount <= 0) {
            Toast.makeText(this, "Dữ liệu không hợp lệ để thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedId == R.id.radioButtonVnpay) {
            // VNPAY Logic remains unchanged

        } else if (selectedId == R.id.radioButtonVietQr) {
            handleVietQrPayment();
        } else if (selectedId == R.id.radioButtonCash) {
            handleCashPayment();
        }
    }

    private void handleVietQrPayment() {
        new Thread(() -> {
            try {
                Payment vietQrPayment = new Payment(bookingId, amount, Payment.PaymentMethod.VIETQR);
                vietQrPayment.setStatus(Payment.PaymentStatus.PENDING);
                long paymentId = paymentViewModel.insert(vietQrPayment).get();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đang tạo mã VietQR...", Toast.LENGTH_SHORT).show();
                    Intent vietQrIntent = new Intent(this, VietQrActivity.class);
                    vietQrIntent.putExtra(EXTRA_BOOKING_ID, bookingId);
                    vietQrIntent.putExtra(EXTRA_AMOUNT, amount);
                    vietQrIntent.putExtra(EXTRA_PAYMENT_ID, paymentId);
                    startActivity(vietQrIntent);
                });

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi tạo thanh toán VietQR", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void handleCashPayment() {
        new Thread(() -> {
            try {
                Payment cashPayment = new Payment(bookingId, amount, Payment.PaymentMethod.CASH);
                cashPayment.setStatus(Payment.PaymentStatus.SUCCESS);
                long paymentId = paymentViewModel.insert(cashPayment).get();

                bookingViewModel.updateBookingStatus(bookingId, Booking.BookingStatus.CHECKED_OUT).get();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                    Intent invoiceIntent = new Intent(this, InvoiceActivity.class);
                    invoiceIntent.putExtra("PAYMENT_ID", paymentId);
                    startActivity(invoiceIntent);
                    finish();
                });

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi lưu thanh toán tiền mặt", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public static String getIPAddress(boolean useIPv4) {
        // ... (existing code)
        return "127.0.0.1";
    }
}
