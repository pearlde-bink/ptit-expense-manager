package com.example.expensemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.expensemanager.api.CategoryService;
import com.example.expensemanager.api.IncomeService;
import com.example.expensemanager.model.Category;
import com.example.expensemanager.model.Income;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;

public class AddIncome extends AppCompatActivity {

    private IncomeService incomeService;
    private CategoryService categoryService;
    private SharedPreferences sharedPreferences;

    private TextInputEditText incomeTitleInput;
    private TextInputEditText amountInput;
    private TextView monthYearText;
    private RecyclerView calendarGrid;
    private CalendarRecyclerAdapter calendarAdapter;
    private Calendar selectedDate;
    private Category selectedCategory; // Chỉ sử dụng một category (category mới nhất)
    private boolean isMonthNavigationEnabled = true;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long DEBOUNCE_DELAY = 300; // 300ms debounce delay

    private List<Category> categoryList; // Danh sách category từ API
    private LinearLayout categoryContainer;
    private List<MaterialButton> categoryButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        // Khởi tạo Retrofit service
        incomeService = ApiClient.getClient().create(IncomeService.class);
        categoryService = ApiClient.getClient().create(CategoryService.class);
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

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

        // Khởi tạo danh sách category
        categoryList = new ArrayList<>();
        categoryButtons = new ArrayList<>();

        // Load categories từ API
//        loadCategoriesFromApi();

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

        // Trong addIncomeButton
        addIncomeButton.setOnClickListener(v -> {
            String incomeTitle = incomeTitleInput.getText().toString().trim();
            String amountStr = amountInput.getText().toString().trim();

            if (incomeTitle.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(AddIncome.this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCategory == null) {
                Toast.makeText(AddIncome.this, "Please add a category first", Toast.LENGTH_SHORT).show();
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

            // Định dạng ngày thành chuỗi ISO 8601 (hoặc định dạng mà backend mong đợi)
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = isoFormat.format(selectedDate.getTime());

//            SimpleDateFormat isoFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            isoFormat.setLenient(false); // tùy chọn: để đảm bảo định dạng nghiêm ngặt
//            Date dateconstructor = null;
//            try {
//                dateconstructor = isoFormat2.parse(formattedDate);
//                // date giờ là kiểu java.util.Date
//            } catch (ParseException e) {
//                e.printStackTrace(); // xử lý lỗi định dạng sai
//            }

            // Chuyển categoryId thành số
            long categoryId;
            try {
                categoryId = Long.parseLong(selectedCategory.getId().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(AddIncome.this, "Invalid category ID", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng Income mới
            Income newIncome = new Income(formattedDate, incomeTitle, amount, categoryId);

            // Log JSON để kiểm tra
            Gson gson = new Gson();
            Log.d("AddIncome", "Request body: " + gson.toJson(newIncome));

            String token = sharedPreferences.getString("accessToken", "");
            Log.d("AddIncome", "Token before API call: " + token);
            if (token.isEmpty()) {
                Toast.makeText(AddIncome.this, "Please log in to continue", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(AddIncome.this, AuthenLogin.class);
                startActivity(loginIntent);
                finish();
                return;
            }

            Call<Income> call = incomeService.createIncome(newIncome, "Bearer " + token);
            call.enqueue(new Callback<Income>() {
                @Override
                public void onResponse(Call<Income> call, Response<Income> response) {
                    Log.d("AddIncome", "Response code: " + response.code());
                    Log.d("AddIncome", "Response message: " + response.message());
                    if (response.code() == 401) {
                        Toast.makeText(AddIncome.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(AddIncome.this, AuthenLogin.class);
                        startActivity(loginIntent);
                        finish();
                        return;
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("new_income", response.body());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        try {
                            Log.e("AddIncome", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("AddIncome", "Error reading error body: " + e.getMessage());
                        }
                        Toast.makeText(AddIncome.this, "Failed to add income: " + response.message(), Toast.LENGTH_SHORT).show();
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
        outState.putString("selectedCategoryTitle", selectedCategory != null ? selectedCategory.getTitle() : null);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedDate.setTimeInMillis(savedInstanceState.getLong("selectedDate", Calendar.getInstance().getTimeInMillis()));
        String savedCategoryTitle = savedInstanceState.getString("selectedCategoryTitle", null);
        selectedCategory = categoryList != null ? categoryList.stream()
                .filter(c -> c.getTitle().equals(savedCategoryTitle))
                .findFirst()
                .orElse(null) : null;
        updateMonthYearText();
        updateSelectedDay();
        updateCategoryButtonStyles();
    }

    private void updateCategoryButtonStyles() {
        for (MaterialButton button : categoryButtons) {
            String buttonText = button.getText().toString();
            String categoryTitle = buttonText.split(" - ")[0];
            boolean isSelected = selectedCategory != null && categoryTitle.equals(selectedCategory.getTitle());
            button.setTextColor(getResources().getColor(isSelected ? android.R.color.white : android.R.color.black));
            button.setBackgroundTintList(getResources().getColorStateList(isSelected ? R.color.Blue : android.R.color.white));
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
        categoryContainer.removeAllViews();
        categoryButtons.clear();
//        if (categoryList != null) {
//            for (Category category : categoryList) {
//                if ("INCOME".equalsIgnoreCase(category.getType())) {
//                    addCategoryButton(category);
//                }
//            }
//        }
        if (selectedCategory != null) {
            addCategoryButton(selectedCategory);
        }
    }

    private void addCategoryButton(Category category) {
        MaterialButton categoryButton = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 8, 0);
        categoryButton.setLayoutParams(params);

        // Hiển thị tên category và icon
        String buttonText = category.getTitle() + " - " + category.getIcon();
        categoryButton.setText(buttonText);
        categoryButton.setTextSize(14);
        categoryButton.setCornerRadius(20);
        boolean isSelected = selectedCategory != null && category.getTitle().equals(selectedCategory.getTitle());
        categoryButton.setTextColor(getResources().getColor(isSelected ? android.R.color.white : android.R.color.black));
        categoryButton.setBackgroundTintList(getResources().getColorStateList(isSelected ? R.color.Blue : android.R.color.white));
        categoryButton.setStrokeColorResource(R.color.LightSlateGray);
        categoryButton.setStrokeWidth(1);

        // Loại bỏ onClickListener vì chỉ dùng category mới nhất
        categoryContainer.addView(categoryButton);
        categoryButtons.add(categoryButton);
    }

    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_category);

        TextInputEditText categoryInput = dialog.findViewById(R.id.category_input);
        AutoCompleteTextView iconDropdown = dialog.findViewById(R.id.icon_dropdown);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);
        MaterialButton addButton = dialog.findViewById(R.id.add_button);

        // Lấy danh sách tên icon
        List<String> iconNames = Arrays.asList(
                "ic_salary", "ic_rewards", "ic_gift", "ic_shopping"
                // ... thêm các tên icon bạn muốn
        );
        ArrayAdapter<String> iconAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, iconNames);
        iconDropdown.setAdapter(iconAdapter);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        addButton.setOnClickListener(v -> {
            String title = categoryInput.getText().toString().trim();
            String icon = iconDropdown.getText().toString().trim();
            if (title.isEmpty() || icon.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo Category mới
            Category newCategory = new Category(0, 0, title, "INCOME", icon);

            // Lấy token
            String accessToken = sharedPreferences.getString("accessToken", "");
            Log.d("AddIncome", "Token in add category: " + accessToken);
            if (accessToken.isEmpty()) {
                Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(AddIncome.this, AuthenLogin.class);
                startActivity(loginIntent);
                finish();
                return;
            }

            // Gọi API để thêm category
            Call<Category> call = categoryService.createCategory(newCategory, "Bearer " + accessToken);
            call.enqueue(new Callback<Category>() {
                @Override
                public void onResponse(Call<Category> call, Response<Category> response) {
                    Log.d("AddIncome", "Response code: " + response.code());
                    Log.d("AddIncome", "Response message: " + response.message());
                    if (response.code() == 401) {
                        Toast.makeText(AddIncome.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(AddIncome.this, AuthenLogin.class);
                        startActivity(loginIntent);
                        finish();
                        return;
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(AddIncome.this, "Category added!", Toast.LENGTH_SHORT).show();
                        // Lấy category mới từ response và đặt làm selectedCategory
                        selectedCategory = response.body();
                        // Làm mới danh sách category
//                        loadCategoriesFromApi();
                        categoryList.clear();
                        categoryList.add(selectedCategory);
                        populateCategoryButtons();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(AddIncome.this, "Failed to add category: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Category> call, Throwable t) {
                    Log.e("AddIncome", "Network error: " + t.getMessage(), t);
                    Toast.makeText(AddIncome.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

//    private void loadCategoriesFromApi() {
//        String accessToken = sharedPreferences.getString("accessToken", "");
//        if (accessToken.isEmpty()) {
//            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
//            Intent loginIntent = new Intent(AddIncome.this, AuthenLogin.class);
//            startActivity(loginIntent);
//            finish();
//            return;
//        }
//
//        Call<List<Category>> call = categoryService.getCategories("Bearer " + accessToken);
//        call.enqueue(new Callback<List<Category>>() {
//            @Override
//            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
//                Log.d("AddIncome", "Response code: " + response.code());
//                Log.d("AddIncome", "Response message: " + response.message());
//                if (response.code() == 401) {
//                    Toast.makeText(AddIncome.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
//                    Intent loginIntent = new Intent(AddIncome.this, AuthenLogin.class);
//                    startActivity(loginIntent);
//                    finish();
//                    return;
//                }
//                if (response.isSuccessful() && response.body() != null) {
//                    categoryList = response.body();
//                    populateCategoryButtons();
//                    // Nếu chưa có selectedCategory, giữ category vừa tạo (nếu có)
//                    if (selectedCategory == null && !categoryList.isEmpty()) {
//                        selectedCategory = categoryList.get(categoryList.size() - 1); // Chọn category mới nhất
//                        updateCategoryButtonStyles();
//                    }
//                } else {
//                    Toast.makeText(AddIncome.this, "Failed to load categories: " + response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Category>> call, Throwable t) {
//                Log.e("AddIncome", "Network error: " + t.getMessage(), t);
//                Toast.makeText(AddIncome.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}