package com.example.projectprmt5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ReportsOverviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_overview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reports Overview");
        }

        Button btnRevenue = findViewById(R.id.btnRevenue);
        Button btnOccupancy = findViewById(R.id.btnOccupancy);
        Button btnFinance = findViewById(R.id.btnFinance);
        Button btnSearch = findViewById(R.id.btnSearchReports);
        Button btnList = findViewById(R.id.btnListReports);

        btnRevenue.setOnClickListener(v -> startActivity(new Intent(this, RevenueReportActivity.class)));
        btnOccupancy.setOnClickListener(v -> startActivity(new Intent(this, OccupancyReportActivity.class)));
        btnFinance.setOnClickListener(v -> startActivity(new Intent(this, FinanceActivity.class)));
        btnSearch.setOnClickListener(v -> startActivity(new Intent(this, ReportsSearchActivity.class)));
        btnList.setOnClickListener(v -> startActivity(new Intent(this, ReportsListActivity.class)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}



