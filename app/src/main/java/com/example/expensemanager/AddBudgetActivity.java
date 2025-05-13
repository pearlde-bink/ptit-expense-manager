package com.example.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.BudgetService;
import com.example.expensemanager.model.Budget;
import com.example.expensemanager.model.BudgetRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBudgetActivity extends BaseActivity {
    private static final String TAG = "AddBudgetActivity";
    private TextInputEditText amountInput;
    private MaterialAutoCompleteTextView monthSpinner, yearSpinner;
    private MaterialButton btnAddBudget;
    private SharedPreferences sharedPref;
    private BudgetService budgetService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Initialize views
        amountInput = findViewById(R.id.amount_input);
        monthSpinner = findViewById(R.id.month_spinner);
        yearSpinner = findViewById(R.id.year_spinner);
        btnAddBudget = findViewById(R.id.btn_add_budget);

        // Setup month spinner
        String[] months = getResources().getStringArray(R.array.months); // Từ res/values/arrays.xml
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, months);
        monthSpinner.setAdapter(monthAdapter);

        // Setup year spinner
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[5]; // 5 năm từ hiện tại
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear + i);
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        yearSpinner.setAdapter(yearAdapter);

        // Setup Retrofit
        budgetService = ApiClient.getClient().create(BudgetService.class);

        // Back arrow click
        findViewById(R.id.back_arrow).setOnClickListener(v -> finish());

        // Add budget button click
        btnAddBudget.setOnClickListener(v -> validateAndAddBudget());

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void validateAndAddBudget() {
        String accessToken = sharedPref.getString("accessToken", "");
        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Please login to add budget", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenLogin.class));
            finish();
            return;
        }

        // Validate amount
        String amountStr = amountInput.getText().toString().trim();
        if (amountStr.isEmpty()) {
            amountInput.setError("Please enter amount");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                amountInput.setError("Amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            amountInput.setError("Invalid amount format");
            return;
        }

        // Validate month
        String monthStr = monthSpinner.getText().toString().trim();
        if (monthStr.isEmpty()) {
            monthSpinner.setError("Please select a month");
            return;
        }
        int month = getMonthIndex(monthStr) + 1; // 1-12
        if (month < 1 || month > 12) {
            monthSpinner.setError("Invalid month");
            return;
        }

        // Validate year
        String yearStr = yearSpinner.getText().toString().trim();
        if (yearStr.isEmpty()) {
            yearSpinner.setError("Please select a year");
            return;
        }
        int year;
        try {
            year = Integer.parseInt(yearStr);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (year < currentYear) {
                yearSpinner.setError("Year must be current year or later");
                return;
            }
        } catch (NumberFormatException e) {
            yearSpinner.setError("Invalid year format");
            return;
        }

        // Get user ID
        int userId = sharedPref.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User information not found. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenLogin.class));
            finish();
            return;
        }

        // Check if budget already exists
        checkBudgetExists(userId, month, year, accessToken, amount);
    }

    private void checkBudgetExists(int userId, int month, int year, String accessToken, double amount) {
        budgetService.getBudgets("Bearer " + accessToken).enqueue(new Callback<List<Budget>>() {
            @Override
            public void onResponse(Call<List<Budget>> call, Response<List<Budget>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exists = response.body().stream()
                            .anyMatch(b -> b.getUserId() == userId && b.getMonth() == month && b.getYear() == year);
                    if (exists) {
                        Toast.makeText(AddBudgetActivity.this, "Budget for this month already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        addBudget(userId, amount, month, year, accessToken);
                    }
                } else {
                    handleApiError(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Budget>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(AddBudgetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addBudget(int userId, double amount, int month, int year, String accessToken) {
        BudgetRequest request = new BudgetRequest(userId, amount, month, year);
        budgetService.createBudget("Bearer " + accessToken, request).enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddBudgetActivity.this, "Budget added successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish(); // Quay về BudgetActivity
                } else {
                    handleApiError(response.code());
                }
            }

            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(AddBudgetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApiError(int responseCode) {
        switch (responseCode) {
            case 400:
                Toast.makeText(this, "Invalid request data", Toast.LENGTH_SHORT).show();
                break;
            case 401:
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AuthenLogin.class));
                finish();
                break;
            case 409:
                Toast.makeText(this, "Budget for this month already exists", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Failed to process request: " + responseCode, Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "Error: " + responseCode);
    }

    private int getMonthIndex(String monthName) {
        String[] months = getResources().getStringArray(R.array.months);
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(monthName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_budget; // Đồng bộ với BudgetActivity
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return null;
    }
}