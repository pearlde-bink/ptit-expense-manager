package com.example.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.BudgetAdapter;
import com.example.expensemanager.api.BudgetService;
import com.example.expensemanager.model.Budget;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BudgetActivity extends BaseActivity {
    private static final String TAG = "BudgetActivity";
    private static final String BASE_URL = "http://192.168.0.102/"; // Cập nhật nếu cần

    private RecyclerView expensesList;
    private TextView totalBudgetText;
    private TextView monthlyBudgetText;
    private TextView monthYearText; // TextView cho tháng/năm
    private BudgetAdapter budgetAdapter;
    private BudgetService budgetService;
    private SharedPreferences sharedPref;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Initialize views
        initializeViews();

        // Set up Retrofit
        setupRetrofit();

        // Set up RecyclerView
        setupRecyclerView();

        // Set up bottom navigation
        setupBottomNavigation();

        // Fetch budget data
        fetchBudgetData();
    }

    private void initializeViews() {
        expensesList = findViewById(R.id.expenses_list);
        totalBudgetText = findViewById(R.id.total_budget);
        monthlyBudgetText = findViewById(R.id.monthly_budget_text);
        monthYearText = findViewById(R.id.month_year_text); // ID mới cho TextView tháng/năm
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        budgetService = retrofit.create(BudgetService.class);
    }

    private void setupRecyclerView() {
        budgetAdapter = new BudgetAdapter(new ArrayList<>(), new ArrayList<>());
        expensesList.setLayoutManager(new LinearLayoutManager(this));
        expensesList.setAdapter(budgetAdapter);
    }

    private void fetchBudgetData() {
        // Get access token from SharedPreferences
        String accessToken = sharedPref.getString("accessToken", "");
        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Please login to view budgets", Toast.LENGTH_SHORT).show();
            // Chuyển về màn hình đăng nhập nếu cần
            startActivity(new Intent(this, AuthenLogin.class));
            finish();
            return;
        }

        // Get current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int currentYear = calendar.get(Calendar.YEAR);

        // Update month/year TextView
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthYearText.setText(monthYearFormat.format(calendar.getTime()));

        // Fetch budgets with token
        budgetService.getBudgets("Bearer " + accessToken).enqueue(new Callback<List<Budget>>() {
            @Override
            public void onResponse(Call<List<Budget>> call, Response<List<Budget>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Budget> budgets = response.body();

                    // Filter budget for current month
                    Budget currentBudget = null;
                    for (Budget budget : budgets) {
                        if (budget.getMonth() == currentMonth && budget.getYear() == currentYear) {
                            currentBudget = budget;
                            break;
                        }
                    }

                    if (currentBudget != null) {
                        // Update UI with current budget
                        totalBudgetText.setText("$" + String.format("%.2f", currentBudget.getAmount()));
                        // Giả sử Budget có trường spentAmount để tính tiến độ
                        double spentAmount = currentBudget.getSpentAmount() != null ? currentBudget.getSpentAmount() : 0.0;
                        monthlyBudgetText.setText(String.format("$%.2f / $%.2f", spentAmount, currentBudget.getAmount()));

                        // Update RecyclerView
                        List<Budget> budgetList = new ArrayList<>();
                        budgetList.add(currentBudget);
                        budgetAdapter.setBudgets(budgetList);

                        // Update progress indicator (giả sử progress dựa trên spentAmount/amount)
                        int progress = (int) ((spentAmount / currentBudget.getAmount()) * 100);
                        com.google.android.material.progressindicator.LinearProgressIndicator progressIndicator =
                                findViewById(R.id.progress_indicator); // ID mới cho LinearProgressIndicator
                        progressIndicator.setProgress(progress);
                    } else {
                        Toast.makeText(BudgetActivity.this, "No budget set for this month", Toast.LENGTH_SHORT).show();
                        totalBudgetText.setText("$0.00");
                        monthlyBudgetText.setText("$0 / $0");
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(BudgetActivity.this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BudgetActivity.this, AuthenLogin.class));
                        finish();
                    } else {
                        Toast.makeText(BudgetActivity.this, "Failed to fetch budget data: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Budget>> call, Throwable t) {
                Log.e(TAG, "Error fetching budget data", t);
                Toast.makeText(BudgetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_budget;
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return AddBudgetActivity.class;
    }
}