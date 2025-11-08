package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import com.example.projectprmt5.R;

/**
 * Màn hình chào mừng với ViewPager giới thiệu
 * Welcome Screen with Onboarding
 */
public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";

    private ViewPager2 viewPager;
    private TabLayout tabIndicator;
    private WelcomePagerAdapter adapter;
    private MaterialButton btnLogin;
    private MaterialButton btnRegister;
    private TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Log.d(TAG, "Welcome screen started");

        initViews();
        setupViewPager();
        setupListeners();
    }

    /**
     * Khởi tạo các views
     */
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabIndicator = findViewById(R.id.tabIndicator);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvSkip = findViewById(R.id.tvSkip);
    }

    /**
     * Setup ViewPager với slides giới thiệu
     */
    private void setupViewPager() {
        // Tạo danh sách slides
        List<WelcomeSlide> slides = new ArrayList<>();
        slides.add(new WelcomeSlide(
                "Quản lý khách sạn chuyên nghiệp",
                "Hệ thống quản lý toàn diện cho khách sạn của bạn"
        ));
        slides.add(new WelcomeSlide(
                "Đặt phòng dễ dàng",
                "Khách hàng có thể đặt phòng nhanh chóng và tiện lợi"
        ));
        slides.add(new WelcomeSlide(
                "Thống kê chi tiết",
                "Theo dõi doanh thu, tỷ lệ lấp đầy và phản hồi khách hàng"
        ));

        // Setup adapter
        adapter = new WelcomePagerAdapter(slides);
        viewPager.setAdapter(adapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabIndicator, viewPager,
                (tab, position) -> {
                    // Không hiển thị text cho tab indicator
                }
        ).attach();
    }

    /**
     * Setup listeners cho buttons
     */
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Log.d(TAG, "Register button clicked");
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvSkip.setOnClickListener(v -> {
            Log.d(TAG, "Skip clicked");
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}

/**
 * Model cho Welcome Slide
 */
class WelcomeSlide {
    private String title;
    private String description;

    public WelcomeSlide(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

/**
 * Adapter cho ViewPager2
 */
class WelcomePagerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<WelcomePagerAdapter.WelcomeViewHolder> {
    private List<WelcomeSlide> slides;

    public WelcomePagerAdapter(List<WelcomeSlide> slides) {
        this.slides = slides;
    }

    @Override
    public WelcomeViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_welcome_slide, parent, false);
        return new WelcomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WelcomeViewHolder holder, int position) {
        WelcomeSlide slide = slides.get(position);
        holder.bind(slide);
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static class WelcomeViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;

        public WelcomeViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDescription = view.findViewById(R.id.tvDescription);
        }

        public void bind(WelcomeSlide slide) {
            tvTitle.setText(slide.getTitle());
            tvDescription.setText(slide.getDescription());
        }
    }
}
