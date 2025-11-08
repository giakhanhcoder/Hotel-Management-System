package com.example.projectprmt5;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.viewmodel.BookingViewModel;
import com.example.projectprmt5.viewmodel.PaymentViewModel;
import com.example.projectprmt5.viewmodel.RoomViewModel;
import com.example.projectprmt5.viewmodel.UserViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {

    private TextView tvCustomerName, tvCustomerEmail, tvRoomName, tvCheckIn, tvCheckOut;
    private TextView tvPaymentDate, tvPaymentMethod, tvTotalAmount, tvStatus;

    private PaymentViewModel paymentViewModel;
    private BookingViewModel bookingViewModel;
    private UserViewModel userViewModel;
    private RoomViewModel roomViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Ánh xạ views
        tvCustomerName = findViewById(R.id.textViewInvoiceCustomerName);
        tvCustomerEmail = findViewById(R.id.textViewInvoiceCustomerEmail);
        tvRoomName = findViewById(R.id.textViewInvoiceRoomName);
        tvCheckIn = findViewById(R.id.textViewInvoiceCheckInDate);
        tvCheckOut = findViewById(R.id.textViewInvoiceCheckOutDate);
        tvPaymentDate = findViewById(R.id.textViewInvoicePaymentDate);
        tvPaymentMethod = findViewById(R.id.textViewInvoicePaymentMethod);
        tvTotalAmount = findViewById(R.id.textViewInvoiceTotalAmount);
        tvStatus = findViewById(R.id.textViewInvoiceStatus);

        // Khởi tạo ViewModels
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        long paymentId = getIntent().getLongExtra("PAYMENT_ID", -1);

        if (paymentId != -1) {
            loadInvoiceData((int) paymentId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadInvoiceData(int paymentId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Payment payment = paymentViewModel.getPaymentByIdSync(paymentId).get();
                if (payment == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: Không tìm thấy thanh toán", Toast.LENGTH_SHORT).show());
                    return;
                }

                Booking booking = bookingViewModel.getBookingByIdSync(payment.getBookingId()).get();
                if (booking == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: Không tìm thấy đơn đặt phòng", Toast.LENGTH_SHORT).show());
                    return;
                }

                User user = userViewModel.getUserByIdSync(booking.getUserId()).get();
                if (user == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: Không tìm thấy người dùng", Toast.LENGTH_SHORT).show());
                    return;
                }

                Room room = roomViewModel.getRoomByIdSync(booking.getRoomId()).get();
                if (room == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: Không tìm thấy phòng", Toast.LENGTH_SHORT).show());
                    return;
                }

                runOnUiThread(() -> populateInvoiceData(payment, booking, user, room));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(InvoiceActivity.this, "Lỗi khi tải dữ liệu hóa đơn", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void populateInvoiceData(Payment payment, Booking booking, User user, Room room) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Thông tin khách hàng
        tvCustomerName.setText("Tên: " + user.getFullName());
        tvCustomerEmail.setText("Email: " + user.getEmail());

        // Thông tin đặt phòng
        tvRoomName.setText("Phòng: " + room.getRoomNumber());
        tvCheckIn.setText("Ngày nhận phòng: " + dateFormat.format(booking.getCheckInDate()));
        tvCheckOut.setText("Ngày trả phòng: " + dateFormat.format(booking.getCheckOutDate()));

        // Thông tin thanh toán
        tvPaymentDate.setText("Ngày thanh toán: " + dateFormat.format(payment.getPaymentDate()));
        tvPaymentMethod.setText("Phương thức: " + payment.getPaymentMethod());
        tvTotalAmount.setText("Tổng tiền: " + currencyFormatter.format(payment.getAmount()));
        tvStatus.setText("Trạng thái: " + payment.getStatus());
    }
}
