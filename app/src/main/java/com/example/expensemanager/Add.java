package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.EntryAdapter;
import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.OverviewService;
import com.example.expensemanager.model.Entry;
import com.example.expensemanager.model.Expense;
import com.example.expensemanager.model.Income;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add extends BaseActivity {

    private RecyclerView entriesRecyclerView;
    private EntryAdapter entryAdapter;
    private List<Entry> entries; // Danh sách hiển thị (đã lọc)
    private List<Entry> allEntries; // Danh sách gốc từ server
    private TabLayout entriesTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        MaterialButton btnAddIncome = findViewById(R.id.btn_add_income);
        MaterialButton btnAddExpense = findViewById(R.id.btn_add_expense);
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

        // Fetch entries from server
        fetchEntriesFromServer();

        // Back Arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Add Income Button
        btnAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add.this, AddIncome.class);
                startActivityForResult(intent, 1);
            }
        });

        // Add Expense Button
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add.this, AddExpense.class);
                startActivityForResult(intent, 2);
            }
        });

        // Set up Bottom Navigation
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
                Log.d("AddTabSelected", "Selected tab position: " + tab.getPosition());
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
        Log.d("AddFilter", "Filtered entries count: " + entries.size() + " for tab: " + tabPosition);
    }

    @Override
    protected int getSelectedNavItemId() {
        return -1; // No item highlighted, as this is an intermediate screen
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return null; // No FAB action since we're already in Add
    }

    private void populateSampleEntries() {
        Calendar calendar = Calendar.getInstance();
    }

    private void fetchEntriesFromServer() {
        String token = "Bearer " + getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");

        OverviewService overviewService = ApiClient.getClient().create(OverviewService.class);
        Call<List<Entry>> call = overviewService.getLastestEntries(token);

        call.enqueue(new Callback<List<Entry>>() {
            @Override
            public void onResponse(Call<List<Entry>> call, Response<List<Entry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEntries.clear();
                    entries.clear();
                    allEntries.addAll(response.body());
                    entries.addAll(allEntries); // Mặc định hiển thị tất cả (tab All)
                    entryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Add.this, "Failed to fetch entries: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Entry>> call, Throwable t) {
                Toast.makeText(Add.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == 1 || requestCode == 2)) {
            fetchEntriesFromServer();
        }
    }
}