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
import com.example.expensemanager.api.ExpenseService;
import com.example.expensemanager.model.Budget;
import com.example.expensemanager.model.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
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
    private ExpenseService expenseService;
    private SharedPreferences sharedPref;
    private List<Budget> budgets;
    private List<Expense> allExpenses = new ArrayList<>(); // Lưu tất cả expenses từ API

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

        // Fetch budget and expense data
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
        expenseService = ApiClient.getClient().create(ExpenseService.class);
    }

    private void setupRecyclerView() {
        budgets = new ArrayList<>();
        budgetAdapter = new BudgetAdapter(budgets);
        budgetsList.setLayoutManager(new LinearLayoutManager(this));
        budgetsList.setAdapter(budgetAdapter);
    }

    private void fetchBudgetData() {
        String accessToken = sharedPref.getString("accessToken", "");
        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Please login to view budgets", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenLogin.class));
            finish();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // 5 (May)
        int currentYear = calendar.get(Calendar.YEAR); // 2025

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
                    } else {
                        currentBudgetText.setText("$0.00");
                    }

                    // Fetch Expenses
                    fetchExpenses(accessToken, currentMonth, currentYear, currentBudget);
                } else {
                    handleError(response.code());
                    // Fetch expenses even if budget fails
                    fetchExpenses(accessToken, currentMonth, currentYear, null);
                }
            }

            @Override
            public void onFailure(Call<List<Budget>> call, Throwable t) {
                Log.e(TAG, "Network error (Budget): " + t.getMessage());
                Toast.makeText(BudgetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Fetch expenses even if budget fails
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentYear = calendar.get(Calendar.YEAR);
                fetchExpenses(accessToken, currentMonth, currentYear, null);
            }
        });
    }

    private void fetchExpenses(String accessToken, int currentMonth, int currentYear, Budget currentBudget) {
        expenseService.getExpenses("Bearer " + accessToken).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                Log.d(TAG, "Expense Request URL: " + call.request().url());
                Log.d(TAG, "Expense Response code: " + response.code());
                Log.d(TAG, "Expense Response body: " + (response.body() != null ? response.body().toString() : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    allExpenses.clear();
                    allExpenses.addAll(response.body());

                    // Calculate total expense for current month
                    double currentTotalExpense = calculateTotalExpenseForMonth(allExpenses, currentMonth, currentYear);
                    if (currentBudget != null) {
                        monthlyBudgetText.setText(String.format("$%.2f / $%.2f", currentTotalExpense, currentBudget.getAmount()));
                    } else {
                        monthlyBudgetText.setText(String.format("$%.2f / $0.00", currentTotalExpense));
                    }

                    // Filter and sort budgets for the last 3 months (excluding current month)
                    List<Budget> recentBudgets = budgets.stream()
                            .filter(b -> !(b.getMonth() == currentMonth && b.getYear() == currentYear))
                            .sorted(Comparator.comparingInt(Budget::getYear)
                                    .thenComparingInt(Budget::getMonth)
                                    .reversed())
                            .limit(3)
                            .collect(Collectors.toList());

                    // Prepare data for RecyclerView
                    List<BudgetWithExpense> displayBudgets = new ArrayList<>();
                    for (Budget budget : recentBudgets) {
                        double totalExpense = calculateTotalExpenseForMonth(allExpenses, budget.getMonth(), budget.getYear());
                        displayBudgets.add(new BudgetWithExpense(budget, totalExpense));
                    }

                    // Update RecyclerView with recent budgets
                    budgetAdapter.setBudgetsWithExpenses(displayBudgets);
                } else {
                    handleError(response.code());
                    // Fallback if expenses fetch fails
                    if (currentBudget != null) {
                        monthlyBudgetText.setText(String.format("$0.00 / $%.2f", currentBudget.getAmount()));
                    } else {
                        monthlyBudgetText.setText("$0.00 / $0.00");
                    }
                    budgetAdapter.setBudgets(budgets.stream()
                            .filter(b -> !(b.getMonth() == currentMonth && b.getYear() == currentYear))
                            .sorted(Comparator.comparingInt(Budget::getYear)
                                    .thenComparingInt(Budget::getMonth)
                                    .reversed())
                            .limit(3)
                            .collect(Collectors.toList()));
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Log.e(TAG, "Network error (Expense): " + t.getMessage());
                Toast.makeText(BudgetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Fallback if expenses fetch fails
                if (currentBudget != null) {
                    monthlyBudgetText.setText(String.format("$0.00 / $%.2f", currentBudget.getAmount()));
                } else {
                    monthlyBudgetText.setText("$0.00 / $0.00");
                }
                budgetAdapter.setBudgets(budgets.stream()
                        .filter(b -> !(b.getMonth() == currentMonth && b.getYear() == currentYear))
                        .sorted(Comparator.comparingInt(Budget::getYear)
                                .thenComparingInt(Budget::getMonth)
                                .reversed())
                        .limit(3)
                        .collect(Collectors.toList()));
            }
        });
    }

    private double calculateTotalExpenseForMonth(List<Expense> expenses, int month, int year) {
        double totalExpense = 0.0;
        for (Expense expense : expenses) {
            try {
                String[] dateFormats = {
                        "yyyy-MM-dd'T'HH:mm:ss'Z'", // ISO 8601 with timezone
                        "yyyy-MM-dd" // Basic date format
                };
                Date date = null;
                for (String format : dateFormats) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
                        dateFormat.setLenient(false);
                        date = dateFormat.parse(expense.getDate());
                        break;
                    } catch (ParseException e) {
                        continue;
                    }
                }
                if (date != null) {
                    Calendar expenseCal = Calendar.getInstance();
                    expenseCal.setTime(date);
                    int expenseMonth = expenseCal.get(Calendar.MONTH) + 1;
                    int expenseYear = expenseCal.get(Calendar.YEAR);
                    if (expenseMonth == month && expenseYear == year) {
                        totalExpense += expense.getAmount();
                    }
                } else {
                    Log.w(TAG, "Unable to parse date: " + expense.getDate());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing expense: " + expense.getDate(), e);
            }
        }
        return totalExpense;
    }

    private void handleError(int responseCode) {
        if (responseCode == 401) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenLogin.class));
            finish();
        } else if (responseCode == 400) {
            // Giả định API trả về 400 với message khi budget tháng đã tồn tại
            Toast.makeText(this, "A budget for the selected month already exists!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to fetch data: " + responseCode, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: " + responseCode);
        }
    }

    // Class to hold Budget and its totalExpense together
    public static class BudgetWithExpense {
        private final Budget budget;
        private final double totalExpense;

        public BudgetWithExpense(Budget budget, double totalExpense) {
            this.budget = budget;
            this.totalExpense = totalExpense;
        }

        public Budget getBudget() {
            return budget;
        }

        public double getTotalExpense() {
            return totalExpense;
        }
    }
}