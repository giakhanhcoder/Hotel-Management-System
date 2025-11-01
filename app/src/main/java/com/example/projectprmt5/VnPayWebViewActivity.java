package com.example.projectprmt5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.viewmodel.BookingViewModel;
import com.example.projectprmt5.viewmodel.PaymentViewModel;

public class VnPayWebViewActivity extends AppCompatActivity {

    private PaymentViewModel paymentViewModel;
    private BookingViewModel bookingViewModel;
    private int bookingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay_web_view);

        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        Intent intent = getIntent();
        String url = intent.getStringExtra("vnpay_url");
        bookingId = intent.getIntExtra(PaymentActivity.EXTRA_BOOKING_ID, -1);

        if (url != null) {
            webView.loadUrl(url);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                 if (url.startsWith(VnPayConfig.vnp_ReturnUrl)) {
                    Uri uri = Uri.parse(url);
                    String responseCode = uri.getQueryParameter("vnp_ResponseCode");

                    if (bookingId != -1) {
                        if ("00".equals(responseCode)) {
                            // Payment successful
                            paymentViewModel.updatePaymentStatusByBookingId(bookingId, "SUCCESS");
                            bookingViewModel.updateBookingStatus(bookingId, Booking.BookingStatus.CHECKED_OUT);
                            Toast.makeText(VnPayWebViewActivity.this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                        } else {
                            // Payment failed or cancelled
                             paymentViewModel.updatePaymentStatusByBookingId(bookingId, "FAILED");
                            Toast.makeText(VnPayWebViewActivity.this, "Thanh toán thất bại.", Toast.LENGTH_LONG).show();
                        }
                    }

                    // Close the WebView and return to the app
                    Intent mainIntent = new Intent(VnPayWebViewActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                    finish();
                }
            }
        });
    }
}
