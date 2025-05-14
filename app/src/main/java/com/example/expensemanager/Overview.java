package com.example.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<Entry> entries; // Danh sách hiển thị (đã lọc)
    private List<Entry> allEntries; // Danh sách gốc từ server
    private TabLayout entriesTabLayout;

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
        entriesTabLayout = findViewById(R.id.entries_tab_layout);

        // Initialize entries list
        entries = new ArrayList<>();
        allEntries = new ArrayList<>();

        // Set up RecyclerView
        entryAdapter = new EntryAdapter(this, entries);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        entriesRecyclerView.setAdapter(entryAdapter);

        // Set up TabLayout
        setupTabLayout();

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
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_profile2)
                        .error(R.drawable.ic_profile2)
                        .into(profileIcon);
            }
        }

        // Profile Icon
        profileIcon.setOnClickListener(v -> {
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

    private void setupTabLayout() {
        // Mặc định chọn tab "All"
        entriesTabLayout.getTabAt(0).select();

        // Lắng nghe sự kiện chọn tab
        entriesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterEntries(tab.getPosition());
                Log.d("TabSelected", "Selected tab position: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Không cần xử lý
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                filterEntries(tab.getPosition());
            }
        });
    }

    private void filterEntries(int tabPosition) {
        entries.clear();
        switch (tabPosition) {
            case 0: // All
                entries.addAll(allEntries);
                break;
            case 1: // Incomes
                entries.addAll(allEntries.stream()
                        .filter(entry -> "income".equalsIgnoreCase(entry.getEntryType()))
                        .collect(Collectors.toList()));
                break;
            case 2: // Expenses
                entries.addAll(allEntries.stream()
                        .filter(entry -> "expense".equalsIgnoreCase(entry.getEntryType()))
                        .collect(Collectors.toList()));
                break;
        }
        entryAdapter.notifyDataSetChanged();
        Log.d("Filter", "Filtered entries count: " + entries.size() + " for tab: " + tabPosition);
    }

    private void fetchOverviewData() {
        OverviewService service = ApiClient.getClient().create(OverviewService.class);
        String accessToken = "Bearer " + getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");

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

                        // Lưu danh sách gốc và hiển thị mặc định (All)
                        List<Entry> serverEntries = overviewResponse.getEntries();
                        allEntries.clear();
                        entries.clear();
                        if (serverEntries != null) {
                            allEntries.addAll(serverEntries);
                            entries.addAll(allEntries); // Mặc định hiển thị tất cả
                        }
                        entryAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(Overview.this, "Error fetching data: " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OverviewResponse> call, Throwable t) {
                Toast.makeText(Overview.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        fetchOverviewData();
    }
}