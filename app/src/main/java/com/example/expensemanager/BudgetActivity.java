package com.example.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.BudgetAdapter;
import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.BudgetService;
import com.example.expensemanager.model.Budget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetActivity extends BaseActivity {
    private static final String TAG = "BudgetActivity";
    private RecyclerView budgetsList;
    private TextView currentBudgetText;
    private TextView monthlyBudgetText;
    private TextView monthYearText;
    private BudgetAdapter budgetAdapter;
    private BudgetService budgetService;
    private SharedPreferences sharedPref;
    private List<Budget> budgets;

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

        // Set up Back Arrow
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> finish());

        // Set up bottom navigation
        setupBottomNavigation();

        // Fetch budget data
        fetchBudgetData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchBudgetData(); // Refresh data each time activity resumes
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_budget;
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return AddBudgetActivity.class;
    }

    private void initializeViews() {
        budgetsList = findViewById(R.id.budgets_list);
        currentBudgetText = findViewById(R.id.current_budget);
        monthlyBudgetText = findViewById(R.id.monthly_budget_text);
        monthYearText = findViewById(R.id.month_year_text);
    }

    private void setupRetrofit() {
        budgetService = ApiClient.getClient().create(BudgetService.class);
    }

    private void setupRecyclerView() {
        budgets = new ArrayList<>();
        budgetAdapter = new BudgetAdapter(budgets);
        budgetsList.setLayoutManager(new LinearLayoutManager(this));
        budgetsList.setAdapter(budgetAdapter);
    }

    private void fetchBudgetData() {
        String accessToken = sharedPref.getString("accessToken", "");
//        Log.d(TAG, "Access Token: " + accessToken);
        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Please login to view budgets", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenLogin.class));
            finish();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthYearText.setText(monthYearFormat.format(calendar.getTime()));

        // Fetch Budgets
        budgetService.getBudgets("Bearer " + accessToken).enqueue(new Callback<List<Budget>>() {
            @Override
            public void onResponse(Call<List<Budget>> call, Response<List<Budget>> response) {
                Log.d(TAG, "Budget Request URL: " + call.request().url());
                Log.d(TAG, "Budget Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    budgets.clear();
                    budgets.addAll(response.body());
                    Toast.makeText(BudgetActivity.this, "Budgets loaded", Toast.LENGTH_SHORT).show();

                    // Find current month's budget
                    Budget currentBudget = budgets.stream()
                            .filter(b -> b.getMonth() == currentMonth && b.getYear() == currentYear)
                            .findFirst()
                            .orElse(null);

                    // Display current month's budget
                    if (currentBudget != null) {
                        currentBudgetText.setText(String.format("$%.2f", currentBudget.getAmount()));
                        monthlyBudgetText.setText(String.format("$0 / $%.2f", currentBudget.getAmount()));
                    } else {
                        currentBudgetText.setText("$0.00");
                        monthlyBudgetText.setText("$0 / $0");
                    }

                    // Get the 3 most recent budgets
                    List<Budget> recentBudgets = budgets.stream()
                            .sorted(Comparator.comparingInt(Budget::getYear)
                                    .thenComparingInt(Budget::getMonth)
                                    .reversed())
                            .limit(3)
                            .collect(Collectors.toList());

                    // Update RecyclerView with recent budgets
                    budgetAdapter.setBudgets(recentBudgets);
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Budget>> call, Throwable t) {
                Log.e(TAG, "Network error (Budget): " + t.getMessage());
                Toast.makeText(BudgetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleError(int responseCode) {
        if (responseCode == 401) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenLogin.class));
            finish();
        } else {
            Toast.makeText(this, "Failed to fetch data: " + responseCode, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: " + responseCode);
        }
    }
}