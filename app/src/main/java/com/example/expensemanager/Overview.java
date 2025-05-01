package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.EntryAdapter;
import com.example.expensemanager.model.Entry;
import com.example.expensemanager.model.Expense;
import com.example.expensemanager.model.Income;
import com.example.expensemanager.model.Reminder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Overview extends BaseActivity {

    private TextView totalSalaryValue;
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
        ImageView profileIcon = findViewById(R.id.profile_icon);
        totalSalaryValue = findViewById(R.id.total_salary_value);
        totalExpenseValue = findViewById(R.id.total_expense_value);
        monthlySavingsValue = findViewById(R.id.monthly_savings_value);
        MaterialButton btnSavings = findViewById(R.id.btn_savings);
        MaterialButton btnRemind = findViewById(R.id.btn_remind);
        MaterialButton btnBudget = findViewById(R.id.btn_budget);
        entriesRecyclerView = findViewById(R.id.entries_recycler_view);

        // Initialize entries list
        entries = new ArrayList<>();
        populateSampleEntries();

        // Calculate and display summary
        updateFinancialSummary();

        // Set up RecyclerView
        entryAdapter = new EntryAdapter(this, entries);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        entriesRecyclerView.setAdapter(entryAdapter);

        // Profile Icon
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Overview.this, "Profile Clicked", Toast.LENGTH_SHORT).show();
                // Navigate to ProfileActivity if implemented
                // Intent intent = new Intent(OverviewActivity.this, ProfileActivity.class);
                // startActivity(intent);
            }
        });

        // Savings Button
        btnSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Overview.this, SavingsActivity.class);
                startActivity(intent);
            }
        });

        // Remind Button
        btnRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Overview.this, ReminderActivity.class);
                startActivity(intent);
            }
        });

        // Budget Button
        btnBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Overview.this, TotalExpenses.class);
                startActivity(intent);
            }
        });

        // Set up Bottom Navigation
        setupBottomNavigation();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_home; // Highlight the "Home" item
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return Add.class; // FAB leads to AddActivity (for adding income/expense)
    }

    private void populateSampleEntries() {
        Calendar calendar = Calendar.getInstance();

        // Food Entry
        calendar.set(2024, Calendar.FEBRUARY, 20);
        entries.add(new Entry(calendar.getTime(), "Food", 20, "Food", 0.5, "Google Pay", false));

        // Uber Entry
        calendar.set(2024, Calendar.MARCH, 13);
        entries.add(new Entry(calendar.getTime(), "Uber", 18, "Uber", 0.83, "Cash", true));

        // Shopping Entry
        calendar.set(2024, Calendar.JUNE, 3);
        entries.add(new Entry(calendar.getTime(), "Shopping", 400, "Shopping", 0.12, "Paytm", true));
    }

    private void updateFinancialSummary() {
        double totalIncome = 0;
        double totalExpense = 0;

        for (Entry entry : entries) {
            if (entry.isExpense()) {
                totalExpense += entry.getAmount();
            } else {
                totalIncome += entry.getAmount();
            }
        }

        double monthlySavings = totalIncome - totalExpense;

        DecimalFormat df = new DecimalFormat("#,##0.00");
        totalSalaryValue.setText("$" + df.format(totalIncome));
        totalExpenseValue.setText("$" + df.format(totalExpense));
        monthlySavingsValue.setText("$" + df.format(monthlySavings));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data.hasExtra("new_income")) {
                Income newIncome = (Income) data.getSerializableExtra("new_income");
                if (newIncome != null) {
                    entries.add(new Entry(
                            newIncome.getDate(),
                            newIncome.getTitle(),
                            newIncome.getAmount(),
                            newIncome.getCategory(),
                            1.0, // Placeholder VAT
                            "Google Pay", // Placeholder payment method
                            false
                    ));
                    entryAdapter.notifyDataSetChanged();
                    updateFinancialSummary();
                }
            } else if (data.hasExtra("new_expense")) {
                Expense newExpense = (Expense) data.getSerializableExtra("new_expense");
                if (newExpense != null) {
                    entries.add(new Entry(
                            newExpense.getDate(),
                            newExpense.getTitle(),
                            newExpense.getAmount(),
                            newExpense.getCategory(),
                            1.0, // Placeholder VAT
                            "Google Pay", // Placeholder payment method
                            true
                    ));
                    entryAdapter.notifyDataSetChanged();
                    updateFinancialSummary();
                }
            }
        }
    }
}