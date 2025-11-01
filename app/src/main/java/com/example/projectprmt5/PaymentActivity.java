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
        } else if (selectedId == R.id.radioButtonVietQr) {
            paymentMethod = Payment.PaymentMethod.VIETQR;
        } else if (selectedId == R.id.radioButtonCash) {
            paymentMethod = Payment.PaymentMethod.CASH;
        }

        if (bookingId != -1 && amount > 0) {
            if (paymentMethod.equals(Payment.PaymentMethod.VNPAY)) {
                // Create a new Payment object with PENDING status
                Payment vnpayPayment = new Payment(bookingId, amount, Payment.PaymentMethod.VNPAY);
                vnpayPayment.setStatus(Payment.PaymentStatus.PENDING);
                paymentViewModel.insert(vnpayPayment);

                // Generate VNPAY URL
                String orderInfo = "Thanh toan don hang " + bookingId;
                String ipAddress = getIPAddress(true);
                String vnpayUrl = VnPayConfig.getPaymentUrl(amount, orderInfo, ipAddress);

                // Start WebView Activity for VNPAY
                Intent vnpayIntent = new Intent(this, VnPayWebViewActivity.class);
                vnpayIntent.putExtra("vnpay_url", vnpayUrl);
                 vnpayIntent.putExtra(EXTRA_BOOKING_ID, bookingId);
                startActivity(vnpayIntent);

                Toast.makeText(this, "Bắt đầu thanh toán với VNPAY...", Toast.LENGTH_SHORT).show();

            } else if (paymentMethod.equals(Payment.PaymentMethod.VIETQR)) {
                // Create a new Payment object with PENDING status
                Payment vietQrPayment = new Payment(bookingId, amount, Payment.PaymentMethod.VIETQR);
                vietQrPayment.setStatus(Payment.PaymentStatus.PENDING);
                paymentViewModel.insert(vietQrPayment);

                // Start VietQrActivity
                Intent vietQrIntent = new Intent(this, VietQrActivity.class);
                vietQrIntent.putExtra(EXTRA_BOOKING_ID, bookingId);
                vietQrIntent.putExtra(EXTRA_AMOUNT, amount);
                startActivity(vietQrIntent);

                Toast.makeText(this, "Đang tạo mã VietQR...", Toast.LENGTH_SHORT).show();

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

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            } 
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IPAddress", ex.toString());
        }
        return "127.0.0.1"; // default to localhost
    }
}
