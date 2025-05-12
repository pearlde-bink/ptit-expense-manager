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
import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.BudgetService;
import com.example.expensemanager.api.ExpenseService;
import com.example.expensemanager.model.Budget;
import com.example.expensemanager.model.Expense;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private ExpenseService expenseService;
    private SharedPreferences sharedPref;

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

        // Fetch budget and expense data
        fetchBudgetData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchBudgetData(); // Refresh danh sách mỗi lần quay lại
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
        expenseService = ApiClient.getClient().create(ExpenseService.class);
    }

    private void setupRecyclerView() {
        budgetAdapter = new BudgetAdapter(new ArrayList<>());
        budgetsList.setLayoutManager(new LinearLayoutManager(this));
        budgetsList.setAdapter(budgetAdapter);
    }

    private void fetchBudgetData() {
        String accessToken = sharedPref.getString("accessToken", "");
        Log.d(TAG, "Access Token: " + accessToken);
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
                if (!response.isSuccessful() || response.body() == null) {
                    handleError(response.code());
                    return;
                }

                List<Budget> budgets = response.body();
                Budget currentBudget = budgets.stream()
                        .filter(b -> b.getMonth() == currentMonth && b.getYear() == currentYear)
                        .findFirst()
                        .orElse(null);

                // Fetch Expenses
                expenseService.getExpenses("Bearer " + accessToken).enqueue(new Callback<List<Expense>>() {
                    @Override
                    public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                        Log.d(TAG, "Expense Request URL: " + call.request().url());
                        Log.d(TAG, "Expense Response code: " + response.code());
                        if (!response.isSuccessful() || response.body() == null) {
                            handleError(response.code());
                            return;
                        }

                        List<Expense> expenses = response.body();

                        // Tính tổng expense cho tháng hiện tại
                        double currentExpense = expenses.stream()
                                .filter(e -> {
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(e.getDate());
                                    return cal.get(Calendar.MONTH) + 1 == currentMonth &&
                                            cal.get(Calendar.YEAR) == currentYear;
                                })
                                .mapToDouble(Expense::getAmount)
                                .sum();

                        // Hiển thị ngân sách tháng hiện tại
                        if (currentBudget != null) {
                            currentBudgetText.setText(String.format("$%.2f", currentBudget.getAmount()));
                            monthlyBudgetText.setText(String.format("$%.2f / $%.2f",
                                    currentExpense, currentBudget.getAmount()));
                        } else {
                            currentBudgetText.setText("$0.00");
                            monthlyBudgetText.setText(String.format("$%.2f / $0", currentExpense));
                        }

                        // Tổng hợp expense/budget cho các tháng trước
                        Map<String, Double> expenseByMonth = expenses.stream()
                                .collect(Collectors.groupingBy(
                                        e -> {
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(e.getDate());
                                            int month = cal.get(Calendar.MONTH) + 1;
                                            int year = cal.get(Calendar.YEAR);
                                            return year + "-" + month;
                                        },
                                        Collectors.summingDouble(Expense::getAmount)
                                ));

                        List<Budget> previousBudgets = budgets.stream()
                                .filter(b -> b.getYear() < currentYear ||
                                        (b.getYear() == currentYear && b.getMonth() < currentMonth))
                                .sorted(Comparator.comparingInt(Budget::getYear)
                                        .thenComparingInt(Budget::getMonth)
                                        .reversed())
                                .collect(Collectors.toList());

                        List<BudgetAdapter.BudgetDisplay> budgetDisplays = new ArrayList<>();
                        for (Budget budget : previousBudgets) {
                            String key = budget.getYear() + "-" + budget.getMonth();
                            double expense = expenseByMonth.getOrDefault(key, 0.0);
                            budgetDisplays.add(new BudgetAdapter.BudgetDisplay(
                                    budget.getMonth(), budget.getYear(), expense, budget.getAmount()));
                        }

                        if (!budgetDisplays.isEmpty()) {
                            budgetAdapter.setBudgets(budgetDisplays);
                            Toast.makeText(BudgetActivity.this, "Budgets loaded", Toast.LENGTH_SHORT).show();
                        } else {
                            budgetAdapter.setBudgets(new ArrayList<>());
                            Toast.makeText(BudgetActivity.this, "No previous budgets found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Expense>> call, Throwable t) {
                        Log.e(TAG, "Network error (Expense): " + t.getMessage());
                        Toast.makeText(BudgetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
            Toast.makeText(BudgetActivity.this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(BudgetActivity.this, AuthenLogin.class));
            finish();
        } else {
            Toast.makeText(BudgetActivity.this, "Failed to fetch data: " + responseCode, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: " + responseCode);
        }
    }
}