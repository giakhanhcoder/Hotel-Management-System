package com.example.projectprmt5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.BookingRepository;
import com.example.projectprmt5.repository.PaymentRepository;
import com.example.projectprmt5.repository.RoomRepository;
import com.example.projectprmt5.repository.UserRepository;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

public class GuestDashboardActivity extends AppCompatActivity {

    private static final String TAG = "GuestDashboardActivity";
    private static final String PREF_NAME = "HotelManagerPrefs";
    private static final String KEY_USER_ID = "userId";

    // UI Components - Profile Section
    private ImageView imgProfile;
    private TextView tvGuestName;
    private TextView tvGuestEmail;
    private MaterialCardView cardProfile;

    // Quick Stats Cards
    private TextView tvUpcomingCount;
    private TextView tvPastCount;
    private TextView tvTotalSpent;

    // Next Check-in Card
    private TextView tvNextCheckInDate;
    private TextView tvDaysUntil;

    // Statistics Card
    private TextView tvTotalBookings;
    private TextView tvAverageBooking;
    private TextView tvNightsStayed;
    private TextView tvLoyaltyPoints;

    // RecyclerViews
    private RecyclerView recyclerUpcomingBookings;
    private RecyclerView recyclerRecentPayments;

    // Quick Action Cards
    private MaterialCardView cardActionFindRoom;
    private MaterialCardView cardActionCreateBooking;
    private MaterialCardView cardActionBookingHistory;
    private MaterialCardView cardActionProfile;

    // Repositories
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private PaymentRepository paymentRepository;
    private RoomRepository roomRepository;

    // Data
    private int currentUserId;
    private List<Booking> allBookings;
    private BookingAdapter bookingAdapter;
    private PaymentAdapter paymentAdapter;

    // Formatters
    private SimpleDateFormat dateFormatter;
    private NumberFormat currencyFormatter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_dashboard);

        initViews();
        initRepositories();
        initFormatters();
        loadCurrentUser();
        setupToolbar();
        setupRecyclerViews();
        setupListeners();
        loadDashboardData();
    }

    private void initViews() {
        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Profile Section
        imgProfile = findViewById(R.id.imgProfile);
        tvGuestName = findViewById(R.id.tvGuestName);
        tvGuestEmail = findViewById(R.id.tvGuestEmail);
        cardProfile = findViewById(R.id.cardProfile);

        // Quick Stats
        tvUpcomingCount = findViewById(R.id.tvUpcomingCount);
        tvPastCount = findViewById(R.id.tvPastCount);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);

        // Next Check-in
        tvNextCheckInDate = findViewById(R.id.tvNextCheckInDate);
        tvDaysUntil = findViewById(R.id.tvDaysUntil);

        // Statistics
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvAverageBooking = findViewById(R.id.tvAverageBooking);
        tvNightsStayed = findViewById(R.id.tvNightsStayed);
        tvLoyaltyPoints = findViewById(R.id.tvLoyaltyPoints);

        // RecyclerViews
        recyclerUpcomingBookings = findViewById(R.id.recyclerUpcomingBookings);
        recyclerRecentPayments = findViewById(R.id.recyclerRecentPayments);

        // Quick Actions
        cardActionFindRoom = findViewById(R.id.cardActionFindRoom);
        cardActionCreateBooking = findViewById(R.id.cardActionCreateBooking);
        cardActionBookingHistory = findViewById(R.id.cardActionBookingHistory);
        cardActionProfile = findViewById(R.id.cardActionProfile);
    }

    private void initRepositories() {
        userRepository = new UserRepository(getApplication());
        bookingRepository = new BookingRepository(getApplication());
        paymentRepository = new PaymentRepository(getApplication());
        roomRepository = new RoomRepository(getApplication());
    }

    private void initFormatters() {
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        currencyFormatter = NumberFormat.getNumberInstance(new Locale("vi"));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    private void loadCurrentUser() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = prefs.getInt(KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }

        // Load user info
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User user = userRepository.getUserByIdSync(currentUserId).get();
                if (user != null) {
                    runOnUiThread(() -> {
                        tvGuestName.setText(user.getFullName() != null ? user.getFullName() : "Khách");
                        tvGuestEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                        
                        // Update toolbar title
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Chào " + (user.getFullName() != null ? user.getFullName() : "bạn"));
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading user", e);
            }
        });
    }

    private void setupRecyclerViews() {
        // Upcoming Bookings - Horizontal
        recyclerUpcomingBookings.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bookingAdapter = new BookingAdapter(new ArrayList<>(), booking -> {
            // Navigate to booking detail
            Intent intent = new Intent(GuestDashboardActivity.this, BookingDetailActivity.class);
            intent.putExtra("bookingId", booking.getBookingId());
            startActivity(intent);
        });
        recyclerUpcomingBookings.setAdapter(bookingAdapter);

        // Recent Payments - Vertical
        recyclerRecentPayments.setLayoutManager(new LinearLayoutManager(this));
        paymentAdapter = new PaymentAdapter(new ArrayList<>(), payment -> {
            // Navigate to payment detail or booking detail
            Intent intent = new Intent(GuestDashboardActivity.this, BookingDetailActivity.class);
            intent.putExtra("bookingId", payment.getBookingId());
            startActivity(intent);
        });
        recyclerRecentPayments.setAdapter(paymentAdapter);
    }

    private void setupListeners() {
        // Profile click
        cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(GuestDashboardActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
        });

        // Quick Actions
        cardActionFindRoom.setOnClickListener(v -> {
            // TODO: Navigate to RoomSearchActivity when created
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(GuestDashboardActivity.this, RoomSearchActivity.class);
            // startActivity(intent);
        });

        cardActionCreateBooking.setOnClickListener(v -> {
            Intent intent = new Intent(GuestDashboardActivity.this, AddBookingActivity.class);
            startActivity(intent);
        });

        cardActionBookingHistory.setOnClickListener(v -> {
            Intent intent = new Intent(GuestDashboardActivity.this, BookingDashboardActivity.class);
            startActivity(intent);
        });

        cardActionProfile.setOnClickListener(v -> {
            Intent intent = new Intent(GuestDashboardActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
        });
    }

    private void loadDashboardData() {
        if (currentUserId == -1) return;

        // Load bookings
        bookingRepository.getBookingsByGuest(currentUserId).observe(this, bookings -> {
            allBookings = bookings != null ? bookings : new ArrayList<>();
            updateBookingStats();
            updateUpcomingBookings();
            updateNextCheckIn();
        });

        // Load recent payments (via bookings)
        loadRecentPayments();
    }

    private void updateBookingStats() {
        if (allBookings == null) return;

        Date now = new Date();
        int upcomingCount = 0;
        int pastCount = 0;
        double totalSpent = 0.0;
        int totalNights = 0;

        for (Booking booking : allBookings) {
            if (booking.getCheckInDate() != null) {
                if (booking.getCheckInDate().after(now) || 
                    (booking.getCheckInDate().before(now) && 
                     booking.getCheckOutDate() != null && 
                     booking.getCheckOutDate().after(now))) {
                    upcomingCount++;
                } else if (booking.getCheckOutDate() != null && booking.getCheckOutDate().before(now)) {
                    pastCount++;
                }
            }

            if (booking.getTotalAmount() > 0 && 
                (booking.getStatus().equals(Booking.BookingStatus.CONFIRMED) ||
                 booking.getStatus().equals(Booking.BookingStatus.CHECKED_IN) ||
                 booking.getStatus().equals(Booking.BookingStatus.CHECKED_OUT))) {
                totalSpent += booking.getTotalAmount();
            }

            // Calculate total nights
            if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
                long diff = booking.getCheckOutDate().getTime() - booking.getCheckInDate().getTime();
                int nights = (int) (diff / (24 * 60 * 60 * 1000));
                totalNights += nights;
            }
        }

        // Update UI
        tvUpcomingCount.setText(String.valueOf(upcomingCount));
        tvPastCount.setText(String.valueOf(pastCount));
        
        if (totalSpent >= 1000000) {
            tvTotalSpent.setText(String.format(Locale.getDefault(), "%.1fM", totalSpent / 1000000.0));
        } else if (totalSpent >= 1000) {
            tvTotalSpent.setText(String.format(Locale.getDefault(), "%.1fK", totalSpent / 1000.0));
        } else {
            tvTotalSpent.setText(String.valueOf((int)totalSpent));
        }

        // Update statistics
        int totalBookingsCount = allBookings.size();
        tvTotalBookings.setText(String.valueOf(totalBookingsCount));

        if (totalBookingsCount > 0) {
            double avg = totalSpent / totalBookingsCount;
            tvAverageBooking.setText(formatCurrency(avg));
        } else {
            tvAverageBooking.setText("0 VND");
        }

        tvNightsStayed.setText(totalNights + " đêm");

        // Calculate loyalty points (1 point per 10,000 VND)
        int points = (int) (totalSpent / 10000);
        tvLoyaltyPoints.setText(formatNumber(points) + " điểm");
    }

    private void updateUpcomingBookings() {
        if (allBookings == null) return;

        Date now = new Date();
        List<Booking> upcoming = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (booking.getCheckInDate() != null && 
                (booking.getCheckInDate().after(now) || 
                 (booking.getCheckInDate().before(now) && 
                  booking.getCheckOutDate() != null && 
                  booking.getCheckOutDate().after(now)))) {
                if (!booking.getStatus().equals(Booking.BookingStatus.CANCELLED) &&
                    !booking.getStatus().equals(Booking.BookingStatus.CHECKED_OUT)) {
                    upcoming.add(booking);
                }
            }
        }

        // Sort by check-in date (earliest first)
        upcoming.sort((b1, b2) -> {
            if (b1.getCheckInDate() == null || b2.getCheckInDate() == null) return 0;
            return b1.getCheckInDate().compareTo(b2.getCheckInDate());
        });

        // Take first 5
        if (upcoming.size() > 5) {
            upcoming = upcoming.subList(0, 5);
        }

        bookingAdapter.updateBookings(upcoming);
    }

    private void updateNextCheckIn() {
        if (allBookings == null) return;

        Date now = new Date();
        Booking nextBooking = null;
        long minDaysUntil = Long.MAX_VALUE;

        for (Booking booking : allBookings) {
            if (booking.getCheckInDate() != null && 
                booking.getCheckInDate().after(now) &&
                !booking.getStatus().equals(Booking.BookingStatus.CANCELLED)) {
                long daysUntil = (booking.getCheckInDate().getTime() - now.getTime()) / (24 * 60 * 60 * 1000);
                if (daysUntil < minDaysUntil) {
                    minDaysUntil = daysUntil;
                    nextBooking = booking;
                }
            }
        }

        if (nextBooking != null) {
            // Create final variables for lambda
            final Booking finalNextBooking = nextBooking;
            final long finalMinDaysUntil = minDaysUntil;
            
            // Load room info
            AppDatabase.databaseWriteExecutor.execute(() -> {
                try {
                    Room room = roomRepository.getRoomByIdSync(finalNextBooking.getRoomId()).get();
                    runOnUiThread(() -> {
                        String roomInfo = room != null ? "Phòng " + room.getRoomNumber() : "";
                        String dateStr = dateFormatter.format(finalNextBooking.getCheckInDate());
                        tvNextCheckInDate.setText(dateStr + " - " + roomInfo);
                        tvDaysUntil.setText((int)finalMinDaysUntil + " ngày");
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error loading room", e);
                    runOnUiThread(() -> {
                        String dateStr = dateFormatter.format(finalNextBooking.getCheckInDate());
                        tvNextCheckInDate.setText(dateStr);
                        tvDaysUntil.setText((int)finalMinDaysUntil + " ngày");
                    });
                }
            });
        } else {
            tvNextCheckInDate.setText("Không có");
            tvDaysUntil.setText("");
        }
    }

    private void loadRecentPayments() {
        // Create final copy of allBookings for lambda
        final List<Booking> bookingsList = allBookings != null ? new ArrayList<>(allBookings) : new ArrayList<>();
        
        // Load payments for current user's bookings
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<Payment> allPayments = paymentRepository.getAllPaymentsSync().get();
                List<Payment> recentPayments = new ArrayList<>();

                // Filter payments for current user's bookings
                for (Payment payment : allPayments) {
                    for (Booking booking : bookingsList) {
                        if (booking.getBookingId() == payment.getBookingId()) {
                            recentPayments.add(payment);
                            break;
                        }
                    }
                }

                // Sort by date (newest first)
                recentPayments.sort((p1, p2) -> {
                    if (p1.getPaymentDate() == null || p2.getPaymentDate() == null) return 0;
                    return p2.getPaymentDate().compareTo(p1.getPaymentDate());
                });

                // Take first 5
                List<Payment> finalPayments;
                if (recentPayments.size() > 5) {
                    finalPayments = new ArrayList<>(recentPayments.subList(0, 5));
                } else {
                    finalPayments = new ArrayList<>(recentPayments);
                }

                final List<Payment> finalPaymentList = finalPayments;
                runOnUiThread(() -> {
                    paymentAdapter.updatePayments(finalPaymentList);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading payments", e);
            }
        });
    }

    private String formatCurrency(double amount) {
        return currencyFormatter.format(amount) + " VND";
    }

    private String formatNumber(int number) {
        return currencyFormatter.format(number);
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(GuestDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            Toast.makeText(this, "Đang đăng xuất...", Toast.LENGTH_SHORT).show();
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
