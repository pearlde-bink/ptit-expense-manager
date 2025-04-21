package com.example.expensemanager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.CalendarRecyclerAdapter;
import com.example.expensemanager.model.Expense;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddExpense extends AppCompatActivity {

    private TextInputEditText expenseTitleInput;
    private TextInputEditText amountInput;
    private TextView monthYearText;
    private RecyclerView calendarGrid;
    private CalendarRecyclerAdapter calendarAdapter;
    private Calendar selectedDate;
    private String selectedCategory = "Health"; // Default category
    private boolean isMonthNavigationEnabled = true;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long DEBOUNCE_DELAY = 300; // 300ms debounce delay

    // Dynamic categories
    private List<String> categories;
    private LinearLayout categoryContainer;
    private List<MaterialButton> categoryButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        monthYearText = findViewById(R.id.month_year);
        ImageView prevMonth = findViewById(R.id.prev_month);
        ImageView nextMonth = findViewById(R.id.next_month);
        calendarGrid = findViewById(R.id.calendar_grid);
        expenseTitleInput = findViewById(R.id.expense_title_input);
        amountInput = findViewById(R.id.amount_input);
        categoryContainer = findViewById(R.id.category_container);
        ImageButton addCategoryButton = findViewById(R.id.add_category_button);
        MaterialButton addExpenseButton = findViewById(R.id.btn_add_expense);

        // Initialize categories
        categories = new ArrayList<>();
        categories.add("Health");
        categories.add("Grocery");
        categoryButtons = new ArrayList<>();

        // Populate category buttons
        populateCategoryButtons();

        // Initialize selected date
        selectedDate = Calendar.getInstance();
        calendarAdapter = new CalendarRecyclerAdapter(this, selectedDate, (position, day, isInCurrentMonth) -> {
            if (!isInCurrentMonth) {
                if (position < calendarAdapter.getSelectedPosition()) {
                    selectedDate.add(Calendar.MONTH, -1);
                } else {
                    selectedDate.add(Calendar.MONTH, 1);
                }
                calendarAdapter.updateMonth(selectedDate);
                updateMonthYearText();
            }
            calendarAdapter.setSelectedPosition(position);
            selectedDate.set(Calendar.DAY_OF_MONTH, day);
        });
        calendarGrid.setLayoutManager(new GridLayoutManager(this, 7));
        calendarGrid.setAdapter(calendarAdapter);
        updateMonthYearText();

        // Select the current day by default
        updateSelectedDay();

        // Set up Back Arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up Previous Month with Debounce
        prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMonthNavigationEnabled) {
                    isMonthNavigationEnabled = false;
                    selectedDate.add(Calendar.MONTH, -1);
                    calendarAdapter.updateMonth(selectedDate);
                    updateMonthYearText();
                    updateSelectedDay();
                    handler.postDelayed(() -> isMonthNavigationEnabled = true, DEBOUNCE_DELAY);
                }
            }
        });

        // Set up Next Month with Debounce
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMonthNavigationEnabled) {
                    isMonthNavigationEnabled = false;
                    selectedDate.add(Calendar.MONTH, 1);
                    calendarAdapter.updateMonth(selectedDate);
                    updateMonthYearText();
                    updateSelectedDay();
                    handler.postDelayed(() -> isMonthNavigationEnabled = true, DEBOUNCE_DELAY);
                }
            }
        });

        // Set up Add Category Button
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });

        // Set up Add Expense Button
        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expenseTitle = expenseTitleInput.getText().toString();
                String amountStr = amountInput.getText().toString();

                if (expenseTitle.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(AddExpense.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedCategory == null) {
                    Toast.makeText(AddExpense.this, "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddExpense.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Expense object
                Expense newExpense = new Expense(selectedDate.getTime(), expenseTitle, amount, selectedCategory);

                // Pass the new Expense back to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_expense", newExpense);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void populateCategoryButtons() {
        if (categoryButtons.isEmpty()) {
            categoryContainer.removeAllViews();
            categoryButtons.clear();

            for (String category : categories) {
                addCategoryButton(category);
            }
        }
    }

    private void addCategoryButton(String category) {
        MaterialButton categoryButton = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        categoryButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        categoryButton.setText(category);
        categoryButton.setTextSize(14);
        categoryButton.setCornerRadius(20);
        categoryButton.setTextColor(getResources().getColor(category.equals(selectedCategory) ? android.R.color.white : android.R.color.black));
        categoryButton.setBackgroundTintList(getResources().getColorStateList(category.equals(selectedCategory) ? R.color.Blue : android.R.color.white));
        categoryButton.setStrokeColorResource(R.color.LightSlateGray);
        categoryButton.setStrokeWidth(1);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) categoryButton.getLayoutParams();
        params.setMargins(0, 0, 8, 0);
        categoryButton.setLayoutParams(params);

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory = category;
                updateCategoryButtonStyles();
            }
        });

        categoryContainer.addView(categoryButton);
        categoryButtons.add(categoryButton);
    }

    private void updateCategoryButtonStyles() {
        for (MaterialButton button : categoryButtons) {
            String buttonText = button.getText().toString();
            button.setTextColor(getResources().getColor(buttonText.equals(selectedCategory) ? android.R.color.white : android.R.color.black));
            button.setBackgroundTintList(getResources().getColorStateList(buttonText.equals(selectedCategory) ? R.color.Blue : android.R.color.white));
        }
    }

    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_category);

        TextInputEditText categoryInput = dialog.findViewById(R.id.category_input);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);
        MaterialButton addButton = dialog.findViewById(R.id.add_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategory = categoryInput.getText().toString().trim();
                if (newCategory.isEmpty()) {
                    Toast.makeText(AddExpense.this, "Please enter a category name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (categories.contains(newCategory)) {
                    Toast.makeText(AddExpense.this, "Category already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                categories.add(newCategory);
                if (selectedCategory == null) {
                    selectedCategory = newCategory;
                }
                addCategoryButton(newCategory);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateMonthYearText() {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
        monthYearText.setText(monthYearFormat.format(selectedDate.getTime()));
    }

    private void updateSelectedDay() {
        int currentDay = selectedDate.get(Calendar.DAY_OF_MONTH);
        Calendar tempCal = (Calendar) selectedDate.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = tempCal.get(Calendar.DAY_OF_WEEK) - 2; // Adjust for Monday as first day
        if (firstDayOfMonth < 0) {
            firstDayOfMonth += 7;
        }
        int position = firstDayOfMonth + (currentDay - 1);
        if (position >= 0 && position < calendarAdapter.getItemCount()) {
            calendarAdapter.setSelectedPosition(position);
        }
    }
}