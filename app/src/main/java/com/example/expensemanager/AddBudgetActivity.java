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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBudgetActivity extends BaseActivity {
    private static final String TAG = "AddBudgetActivity";
    private TextInputEditText amountInput;
    private MaterialAutoCompleteTextView monthSpinner;
    private TextInputEditText yearInput;
    private MaterialButton btnAddBudget;
    private SharedPreferences sharedPref;
    private BudgetService budgetService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Debug SharedPreferences
        Log.d(TAG, "accessToken: " + sharedPref.getString("accessToken", ""));
        Log.d(TAG, "userId: " + sharedPref.getInt("userId", -1));

        // Initialize views
        amountInput = findViewById(R.id.amount_input);
        monthSpinner = findViewById(R.id.month_spinner);
        yearInput = findViewById(R.id.year_input);
        btnAddBudget = findViewById(R.id.btn_add_budget);

        // Setup month spinner
        String[] months = getResources().getStringArray(R.array.months);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, months);
        monthSpinner.setAdapter(monthAdapter);

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
        String yearStr = yearInput.getText().toString().trim();
        if (yearStr.isEmpty()) {
            yearInput.setError("Please enter a year");
            return;
        }
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            yearInput.setError("Invalid year format");
            return;
        }

        // Validate year and month (not before current date)
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR); // 2025
        int currentMonth = currentDate.get(Calendar.MONTH) + 1; // 5 (May)

        if (year < currentYear) {
            yearInput.setError("Year cannot be before " + currentYear);
            return;
        }
        if (year == currentYear && month < currentMonth) {
            monthSpinner.setError("Month cannot be before " + getResources().getStringArray(R.array.months)[currentMonth - 1]);
            return;
        }

        // Create Budget object and call API
        Budget budget = new Budget(amount, month, year);
        Call<Budget> call = budgetService.createBudget("Bearer " + accessToken, budget);
        call.enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddBudgetActivity.this, "Budget added successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(AddBudgetActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApiError(Response<Budget> response) {
        int responseCode = response.code();
        String errorMessage = "Failed to add budget: " + responseCode;

        // Try to extract error message from response body
        if (responseCode == 400) {
            try {
                ResponseBody errorBody = response.errorBody();
                if (errorBody != null) {
                    errorMessage = errorBody.string();
                    // Assuming the backend returns a simple message or JSON like {"error": "Budget for this month already exists!"}
                    if (errorMessage.contains("already exists")) {
                        errorMessage = "Budget for this month already exists!";
                    }
                } else {
                    errorMessage = "Budget for this month already exists!";
                }
            } catch (IOException e) {
                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                errorMessage = "Budget for this month already exists!";
            }
        } else if (responseCode == 401) {
            errorMessage = "Session expired. Please login again.";
            startActivity(new Intent(this, AuthenLogin.class));
            finish();
        } else if (responseCode == 409) {
            errorMessage = "Budget for this month already exists!";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error: " + responseCode + ", Message: " + errorMessage);
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
        return R.id.nav_budget;
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return null;
    }
}