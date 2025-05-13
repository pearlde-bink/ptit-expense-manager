package com.example.expensemanager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.adapter.CalendarRecyclerAdapter;
import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.IncomeService;
import com.example.expensemanager.model.Income;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;

public class AddIncome extends AppCompatActivity {

    private IncomeService incomeService;
    private SharedPreferences sharedPreferences;

    private TextInputEditText incomeTitleInput;
    private TextInputEditText amountInput;
    private TextView monthYearText;
    private RecyclerView calendarGrid;
    private CalendarRecyclerAdapter calendarAdapter;
    private Calendar selectedDate;
    private String selectedCategory = "Salary"; // Default category
    private boolean isMonthNavigationEnabled = true;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long DEBOUNCE_DELAY = 300; // 300ms debounce delay

    private List<String> categories;
    private LinearLayout categoryContainer;
    private List<MaterialButton> categoryButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        // Khởi tạo Retrofit service
        incomeService = ApiClient.getClient().create(IncomeService.class);
        String accessToken = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("accessToken", "");
        Log.d("AddIncome", "Token: " + accessToken);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        monthYearText = findViewById(R.id.month_year);
        ImageView prevMonth = findViewById(R.id.prev_month);
        ImageView nextMonth = findViewById(R.id.next_month);
        calendarGrid = findViewById(R.id.calendar_grid);
        incomeTitleInput = findViewById(R.id.income_title_input);
        amountInput = findViewById(R.id.amount_input);
        categoryContainer = findViewById(R.id.category_container);
        ImageButton addCategoryButton = findViewById(R.id.add_category_button);
        MaterialButton addIncomeButton = findViewById(R.id.btn_add_income);

        // Load categories from SharedPreferences
        loadCategories();
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
        backArrow.setOnClickListener(v -> finish());

        // Set up Previous Month with Debounce
        prevMonth.setOnClickListener(v -> {
            if (isMonthNavigationEnabled) {
                isMonthNavigationEnabled = false;
                selectedDate.add(Calendar.MONTH, -1);
                calendarAdapter.updateMonth(selectedDate);
                updateMonthYearText();
                updateSelectedDay();
                handler.postDelayed(() -> isMonthNavigationEnabled = true, DEBOUNCE_DELAY);
            }
        });

        // Set up Next Month with Debounce
        nextMonth.setOnClickListener(v -> {
            if (isMonthNavigationEnabled) {
                isMonthNavigationEnabled = false;
                selectedDate.add(Calendar.MONTH, 1);
                calendarAdapter.updateMonth(selectedDate);
                updateMonthYearText();
                updateSelectedDay();
                handler.postDelayed(() -> isMonthNavigationEnabled = true, DEBOUNCE_DELAY);
            }
        });

        // Set up Add Category Button
        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());

        // Set up Add Income Button
        // Trong phần xử lý click của addIncomeButton
        addIncomeButton.setOnClickListener(v -> {
            String incomeTitle = incomeTitleInput.getText().toString().trim();
            String amountStr = amountInput.getText().toString().trim();

            if (incomeTitle.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(AddIncome.this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCategory == null) {
                Toast.makeText(AddIncome.this, getString(R.string.select_category), Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount < 0) {
                    Toast.makeText(AddIncome.this, getString(R.string.amount_negative), Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AddIncome.this, getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo Income object với category name trực tiếp
            Income newIncome = new Income(selectedDate.getTime(), incomeTitle, amount, selectedCategory);

            if (accessToken.isEmpty()) {
                Toast.makeText(AddIncome.this, "Authentication error", Toast.LENGTH_SHORT).show();
                return;
            }

            Call<Income> call = incomeService.createIncome(newIncome, "Bearer " + accessToken);
            call.enqueue(new Callback<Income>() {
                @Override
                public void onResponse(Call<Income> call, Response<Income> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("new_income", response.body());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        String errorMessage = "Failed to add income";
                        try {
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(AddIncome.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Income> call, Throwable t) {
                    Log.e("AddIncome", "Network error: " + t.getMessage(), t);
                    Toast.makeText(AddIncome.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("selectedDate", selectedDate.getTimeInMillis());
        outState.putString("selectedCategory", selectedCategory);
        outState.putStringArrayList("categories", new ArrayList<>(categories));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedDate.setTimeInMillis(savedInstanceState.getLong("selectedDate", Calendar.getInstance().getTimeInMillis()));
        selectedCategory = savedInstanceState.getString("selectedCategory", "Salary");
        categories = savedInstanceState.getStringArrayList("categories");
        if (categories == null || categories.isEmpty()) {
            categories = new ArrayList<>(Arrays.asList("Salary", "Rewards"));
        }
        populateCategoryButtons();
        updateMonthYearText();
        updateSelectedDay();
        updateCategoryButtonStyles();
    }

    private void updateCategoryButtonStyles() {
        for (MaterialButton button : categoryButtons) {
            String buttonText = button.getText().toString();
            button.setTextColor(getResources().getColor(buttonText.equals(selectedCategory) ? android.R.color.white : android.R.color.black));
            button.setBackgroundTintList(getResources().getColorStateList(buttonText.equals(selectedCategory) ? R.color.Blue : android.R.color.white));
        }
    }

    private void updateMonthYearText() {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
        monthYearText.setText(monthYearFormat.format(selectedDate.getTime()));
    }

    private void updateSelectedDay() {
        int currentDay = selectedDate.get(Calendar.DAY_OF_MONTH);
        Calendar tempCal = (Calendar) selectedDate.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = tempCal.get(Calendar.DAY_OF_WEEK) - 2;
        if (firstDayOfMonth < 0) {
            firstDayOfMonth += 7;
        }
        int position = firstDayOfMonth + (currentDay - 1);
        if (position >= 0 && position < calendarAdapter.getItemCount()) {
            calendarAdapter.setSelectedPosition(position);
        }
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

        categoryButton.setOnClickListener(v -> {
            selectedCategory = category;
            updateCategoryButtonStyles();
        });

        categoryContainer.addView(categoryButton);
        categoryButtons.add(categoryButton);
    }

    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_category);

        TextInputEditText categoryInput = dialog.findViewById(R.id.category_input);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);
        MaterialButton addButton = dialog.findViewById(R.id.add_button);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        addButton.setOnClickListener(v -> {
            String newCategory = categoryInput.getText().toString().trim();
            if (newCategory.isEmpty()) {
                Toast.makeText(AddIncome.this, getString(R.string.category_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            if (categories.contains(newCategory)) {
                Toast.makeText(AddIncome.this, getString(R.string.category_exists), Toast.LENGTH_SHORT).show();
                return;
            }

            categories.add(newCategory);
            saveCategories();
            if (selectedCategory == null) {
                selectedCategory = newCategory;
            }
            addCategoryButton(newCategory);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void loadCategories() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String categoriesJson = prefs.getString("categories", null);
        if (categoriesJson != null) {
            Type listType = new TypeToken<ArrayList<String>>(){}.getType();
            categories = new Gson().fromJson(categoriesJson, listType);
        }
        if (categories == null || categories.isEmpty()) {
            categories = new ArrayList<>(Arrays.asList("Salary", "Rewards"));
        }
    }

    private void saveCategories() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("categories", new Gson().toJson(categories));
        editor.apply();
    }
}