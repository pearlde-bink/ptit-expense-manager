package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.expensemanager.model.Income;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class AddIncome extends AppCompatActivity {
    private TextInputEditText incomeTitleInput;
    private TextInputEditText amountInput;
    private MaterialButton categorySalaryButton;
    private MaterialButton categoryRewardsButton;
    private TextView monthYearText;
    private CalendarView calendarView;
    private Calendar selectedDate;
    private String selectedCategory = "Salary"; // Default category

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        monthYearText = findViewById(R.id.month_year);
        ImageView prevMonth = findViewById(R.id.prev_month);
        ImageView nextMonth = findViewById(R.id.next_month);
        calendarView = findViewById(R.id.calendar_view);
        incomeTitleInput = findViewById(R.id.income_title_input);
        amountInput = findViewById(R.id.amount_input);
        categorySalaryButton = findViewById(R.id.category_salary);
        categoryRewardsButton = findViewById(R.id.category_rewards);
        MaterialButton addIncomeButton = findViewById(R.id.btn_add_income);

        // Initialize selected date
        selectedDate = Calendar.getInstance();
        updateMonthYearText();

        // Set up Back Arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up Previous Month
        prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate.add(Calendar.MONTH, -1);
                calendarView.setDate(selectedDate.getTimeInMillis());
                updateMonthYearText();
            }
        });

        // Set up Next Month
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate.add(Calendar.MONTH, 1);
                calendarView.setDate(selectedDate.getTimeInMillis());
                updateMonthYearText();
            }
        });

        // Set up Calendar View
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate.set(year, month, dayOfMonth);
                updateMonthYearText();
            }
        });

        // Set up Category Buttons
        categorySalaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory = "Salary";
                categorySalaryButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue));
                categorySalaryButton.setTextColor(getResources().getColor(android.R.color.white));
                categoryRewardsButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
                categoryRewardsButton.setTextColor(getResources().getColor(android.R.color.black));
            }
        });

        categoryRewardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory = "Rewards";
                categoryRewardsButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue));
                categoryRewardsButton.setTextColor(getResources().getColor(android.R.color.white));
                categorySalaryButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
                categorySalaryButton.setTextColor(getResources().getColor(android.R.color.black));
            }
        });

        // Set up Add Income Button
        addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String incomeTitle = incomeTitleInput.getText().toString();
                String amountStr = amountInput.getText().toString();

                if (incomeTitle.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(AddIncomeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddIncomeActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Income object
                Income newIncome = new Income(selectedDate.getTime(), incomeTitle, amount, selectedCategory);

                // Pass the new Income back to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_income", newIncome);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void updateMonthYearText() {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
        monthYearText.setText(monthYearFormat.format(selectedDate.getTime()));
    }
}