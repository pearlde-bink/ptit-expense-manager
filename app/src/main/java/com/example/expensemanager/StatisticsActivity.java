package com.example.expensemanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.BudgetService;
import com.example.expensemanager.model.Budget;
import com.example.expensemanager.model.BudgetOverview;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatisticsActivity extends BaseActivity {
    private static final String TAG = "StatisticsActivity";

    private TextView totalStatsText;
    private BarChart barChart;
    private TextView budgetListText;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private Button applyButton;
    private int selectedMonth = 5; // Mặc định là tháng 5 (tháng hiện tại)
    private int selectedYear = 2025; // Mặc định là năm 2025

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);

        totalStatsText = findViewById(R.id.total_stats_text);
        barChart = findViewById(R.id.bar_chart);
        budgetListText = findViewById(R.id.budget_list_text);
        monthSpinner = findViewById(R.id.month_spinner);
        yearSpinner = findViewById(R.id.year_spinner);
        applyButton = findViewById(R.id.apply_button);

        setupBarChart();
        setupSpinners();
        loadStatistics();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.setNoDataText("No chart data available");
    }

    private void setupSpinners() {
        // Cài đặt adapter cho monthSpinner
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setSelection(selectedMonth - 1); // Chọn tháng 5 mặc định
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = position + 1; // Lưu tháng đã chọn
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì khi không chọn
            }
        });

        // Cài đặt adapter cho yearSpinner
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(0); // Chọn năm 2025 mặc định
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = Integer.parseInt(parent.getItemAtPosition(position).toString()); // Lưu năm đã chọn
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì khi không chọn
            }
        });

        // Xử lý nút Apply
        applyButton.setOnClickListener(v -> loadStatistics());
    }

    private void loadStatistics() {
        // Lấy token từ SharedPreferences
        String token = getTokenFromSharedPreferences();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "No authentication token found. Please log in.", Toast.LENGTH_LONG).show();
            return;
        }

        BudgetService budgetService = ApiClient.getClient().create(BudgetService.class);

        Call<BudgetOverview> overviewCall = budgetService.getBudgetOverview(
                "Bearer " + token,
                selectedMonth,
                selectedYear
        );
        overviewCall.enqueue(new Callback<BudgetOverview>() {
            @Override
            public void onResponse(Call<BudgetOverview> call, Response<BudgetOverview> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BudgetOverview overview = response.body();
                    totalStatsText.setText("Income: $" + String.format("%.2f", overview.getTotalIncome()) +
                            " | Expense: $" + String.format("%.2f", overview.getTotalExpense()));

                    List<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0f, (float) overview.getTotalIncome()));
                    entries.add(new BarEntry(1f, (float) -overview.getTotalExpense())); // Negative for expense

                    if (entries.isEmpty() || (overview.getTotalIncome() == 0 && overview.getTotalExpense() == 0)) {
                        barChart.setData(null);
                        barChart.invalidate();
                        Toast.makeText(StatisticsActivity.this, "No data to display in chart", Toast.LENGTH_SHORT).show();
                    } else {
                        BarDataSet dataSet = new BarDataSet(entries, "Income & Expense");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dataSet.setValueTextSize(12f);

                        BarData barData = new BarData(dataSet);
                        barChart.setData(barData);
                        barChart.invalidate(); // Refresh chart
                    }

                    budgetListText.setText("Budgets not available in this response.");
                } else {
                    Toast.makeText(StatisticsActivity.this, "Failed to load stats: " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BudgetOverview> call, Throwable t) {
                Toast.makeText(StatisticsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failure: " + t.getMessage(), t);
            }
        });
    }

    // Phương thức lấy token từ SharedPreferences
    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPref.getString("accessToken", null);
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_statistic;
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return null;
    }
}