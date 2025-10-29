package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
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

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "com.example.projectprmt5.EXTRA_BOOKING_ID";
    public static final String EXTRA_AMOUNT = "com.example.projectprmt5.EXTRA_AMOUNT";

    private TextView textViewBookingInfo, textViewAmount;
    private RadioGroup radioGroupPaymentMethods;
    private Button buttonProceedToPayment;

    private PaymentViewModel paymentViewModel;
    private BookingViewModel bookingViewModel; // Added BookingViewModel
    private int bookingId;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize ViewModels
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class); // Initialize BookingViewModel

        // Find views
        textViewBookingInfo = findViewById(R.id.textViewBookingInfo);
        textViewAmount = findViewById(R.id.textViewAmount);
        radioGroupPaymentMethods = findViewById(R.id.radioGroupPaymentMethods);
        buttonProceedToPayment = findViewById(R.id.buttonProceedToPayment);
        
        // Get data from intent
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_BOOKING_ID) && intent.hasExtra(EXTRA_AMOUNT)) {
            bookingId = intent.getIntExtra(EXTRA_BOOKING_ID, -1);
            amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0.0);

            // Display information
            textViewBookingInfo.setText("Thanh toán cho Đơn đặt phòng #: " + bookingId);
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            textViewAmount.setText("Số tiền: " + currencyFormatter.format(amount));
            
        } else {
            Toast.makeText(this, "Thông tin thanh toán không hợp lệ", Toast.LENGTH_LONG).show();
            finish(); // Close activity if data is missing
        }

        // Set default payment method
        ((RadioButton)findViewById(R.id.radioButtonVnpay)).setChecked(true);

        // Set listener for payment button
        buttonProceedToPayment.setOnClickListener(v -> handlePayment());
    }

    private void handlePayment() {
        int selectedId = radioGroupPaymentMethods.getCheckedRadioButtonId();
        String paymentMethod = "";

        if (selectedId == R.id.radioButtonVnpay) {
            paymentMethod = Payment.PaymentMethod.VNPAY;
        } else if (selectedId == R.id.radioButtonCash) {
            paymentMethod = Payment.PaymentMethod.CASH;
        }

        if (bookingId != -1 && amount > 0) {
            if (paymentMethod.equals(Payment.PaymentMethod.VNPAY)) {
                // TODO: Implement VNPAY integration
                Toast.makeText(this, "Bắt đầu thanh toán với VNPAY...", Toast.LENGTH_SHORT).show();
                // 1. Create a new Payment object with PENDING status
                // 2. Save it to the database via ViewModel
                // 3. Generate VNPAY URL and open WebView

            } else if (paymentMethod.equals(Payment.PaymentMethod.CASH)) {
                // Create a new Payment object with SUCCESS status
                Payment cashPayment = new Payment(bookingId, amount, Payment.PaymentMethod.CASH);
                cashPayment.setStatus(Payment.PaymentStatus.SUCCESS);
                paymentViewModel.insert(cashPayment);

                // Update booking status to CHECKED_OUT
                bookingViewModel.updateBookingStatus(bookingId, Booking.BookingStatus.CHECKED_OUT);
                
                Toast.makeText(this, "Thanh toán thành công và hoàn tất Check-out!", Toast.LENGTH_LONG).show();
                finish(); // Close payment activity
            }
        } else {
            Toast.makeText(this, "Dữ liệu không hợp lệ để thanh toán", Toast.LENGTH_SHORT).show();
        }
    }
}
