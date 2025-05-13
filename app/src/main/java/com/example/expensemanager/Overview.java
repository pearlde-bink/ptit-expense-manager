package com.example.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expensemanager.adapter.EntryAdapter;
import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.OverviewService;
import com.example.expensemanager.model.Entry;
import com.example.expensemanager.model.OverviewResponse;
import com.example.expensemanager.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Overview extends BaseActivity {

    private TextView totalSalaryValue;
    private ImageView profileIcon;
    private TextView totalExpenseValue;
    private TextView monthlySavingsValue;
    private RecyclerView entriesRecyclerView;
    private EntryAdapter entryAdapter;
    private List<Entry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // Initialize views
        profileIcon = findViewById(R.id.profile_icon);
        totalSalaryValue = findViewById(R.id.total_salary_value);
        totalExpenseValue = findViewById(R.id.total_expense_value);
        monthlySavingsValue = findViewById(R.id.monthly_savings_value);
        MaterialButton btnSavings = findViewById(R.id.btn_savings);
        MaterialButton btnBudget = findViewById(R.id.btn_budget);
        entriesRecyclerView = findViewById(R.id.entries_recycler_view);

        // Initialize entries list
        entries = new ArrayList<>();

        // Set up RecyclerView
        entryAdapter = new EntryAdapter(this, entries);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        entriesRecyclerView.setAdapter(entryAdapter);

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userJson = sharedPref.getString("user", null);

        // Nếu có dữ liệu user, giải mã JSON thành đối tượng User
        if (userJson != null) {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            // Lấy URL avatar
            String avatarUrl = user.getAvatar();
            // Dùng Glide để tải ảnh vào ImageView
            if (avatarUrl != null) {
                Glide.with(this)
                        .load(avatarUrl)  // URL của ảnh từ Cloudinary
                        .placeholder(R.drawable.ic_avatar)  // Hình ảnh thay thế khi chưa tải xong
                        .error(R.drawable.ic_avatar)  // Hình ảnh hiển thị nếu có lỗi
                        .into(profileIcon);  // ImageView nơi hiển thị ảnh
            }
        }

        // Profile Icon
        profileIcon.setOnClickListener(v -> {
            // Tạo một Intent để chuyển tới User_Profile Activity
            Intent intent = new Intent(Overview.this, User_Profile.class);
            startActivity(intent);
        });

        // Savings Button
        btnSavings.setOnClickListener(v -> {
            Intent intent = new Intent(Overview.this, SavingsActivity.class);
            startActivity(intent);
        });

        // Budget Button
        btnBudget.setOnClickListener(v -> {
            Intent intent = new Intent(Overview.this, TotalExpenses.class);
            startActivity(intent);
        });

        // Fetch overview data
        fetchOverviewData();

        setupBottomNavigation();
    }

    private void fetchOverviewData() {
        // Assuming you're using Retrofit to get the data
        OverviewService service = ApiClient.getClient().create(OverviewService.class);
        String accessToken = "Bearer " + getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");

        // Call the API
        Call<OverviewResponse> call = service.getOverviewData(accessToken);
        call.enqueue(new Callback<OverviewResponse>() {
            @Override
            public void onResponse(Call<OverviewResponse> call, Response<OverviewResponse> response) {
                if (response.isSuccessful()) {
                    OverviewResponse overviewResponse = response.body();
                    if (overviewResponse != null) {
                        // Update tổng số tiền
                        totalSalaryValue.setText("$" + overviewResponse.getTotalIncome());
                        totalExpenseValue.setText("$" + overviewResponse.getTotalExpense());
                        monthlySavingsValue.setText("$" + overviewResponse.getCurrentAmount());

                        // Dùng luôn dữ liệu từ server, không convert
                        // Xử lý entries cẩn thận tránh null
                        List<Entry> serverEntries = overviewResponse.getEntries();
                        entries.clear();
                        if (serverEntries != null) {
                            entries.addAll(serverEntries);
                        }
                        entryAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(Overview.this, "Error fetching data" + response.code() + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override

            public void onFailure(Call<OverviewResponse> call, Throwable t) {
                Toast.makeText(Overview.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_home; // Highlight the "Home" item
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return Add.class; // FAB leads to AddActivity (for adding income/expense)
    }
}