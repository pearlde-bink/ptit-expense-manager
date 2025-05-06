package com.example.expensemanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensemanager.api.BudgetService;
import com.example.expensemanager.model.BudgetRequest;
import com.example.expensemanager.model.Budget;
import com.google.android.material.button.MaterialButton;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddBudgetActivity extends AppCompatActivity {
    private EditText amountInput;
    private Spinner monthSpinner, yearSpinner, categorySpinner;
    private MaterialButton btnAddBudget;
    private final String BASE_URL = "http://10.0.3.2:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        amountInput = findViewById(R.id.amount_input);
        monthSpinner = findViewById(R.id.month_spinner);
        yearSpinner = findViewById(R.id.year_spinner);
        categorySpinner = findViewById(R.id.category_spinner);
        btnAddBudget = findViewById(R.id.btn_add_budget);

        // Setup month spinner
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) months[i] = String.valueOf(i + 1);
        monthSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months));

        // Setup year spinner
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[]{String.valueOf(currentYear), String.valueOf(currentYear + 1)};
        yearSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years));

        // Setup category spinner (giả lập, bạn có thể lấy từ API hoặc truyền vào)
        String[] categories = new String[]{"Food", "Transport", "Shopping", "Other"};
        categorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));

        btnAddBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = amountInput.getText().toString();
                if (amountStr.isEmpty()) {
                    Toast.makeText(AddBudgetActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount = Double.parseDouble(amountStr);
                int month = Integer.parseInt(monthSpinner.getSelectedItem().toString());
                int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                int categoryId = categorySpinner.getSelectedItemPosition() + 1; // Giả lập id
                int userId = 1; // Giả lập userId, thực tế lấy từ user đang đăng nhập

                BudgetRequest request = new BudgetRequest(userId, categoryId, amount, month, year);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BudgetService budgetService = retrofit.create(BudgetService.class);
                budgetService.createBudget(request).enqueue(new Callback<Budget>() {
                    @Override
                    public void onResponse(Call<Budget> call, Response<Budget> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AddBudgetActivity.this, "Budget added!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddBudgetActivity.this, "Failed to add budget", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Budget> call, Throwable t) {
                        Toast.makeText(AddBudgetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}