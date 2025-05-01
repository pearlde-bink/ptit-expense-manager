package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.EntryAdapter;
import com.example.expensemanager.model.Entry;
import com.example.expensemanager.model.Expense;
import com.example.expensemanager.model.Income;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Add extends BaseActivity {

    private RecyclerView entriesRecyclerView;
    private EntryAdapter entryAdapter;
    private List<Entry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        MaterialButton btnAddIncome = findViewById(R.id.btn_add_income);
        MaterialButton btnAddExpense = findViewById(R.id.btn_add_expense);
        entriesRecyclerView = findViewById(R.id.entries_recycler_view);

        // Initialize entries list
        entries = new ArrayList<>();
        populateSampleEntries();

        // Set up RecyclerView
        entryAdapter = new EntryAdapter(this, entries);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        entriesRecyclerView.setAdapter(entryAdapter);

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

        // Salary Entry
        calendar.set(2024, Calendar.FEBRUARY, 20);
        entries.add(new Entry(calendar.getTime(), "Salary", 20, "Salary", 0.5, "Google Pay", false));

        // Cashback Entry
        calendar.set(2024, Calendar.MARCH, 13);
        entries.add(new Entry(calendar.getTime(), "Cashback", 1400, "Cashback", 1.0, "Cash", false));

        // Price Entry
        calendar.set(2024, Calendar.JUNE, 3);
        entries.add(new Entry(calendar.getTime(), "Price", 120, "Money", 1.0, "Paytm", false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                // Handle new income
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
                    // Forward the result to the calling activity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("new_income", newIncome);
                    setResult(RESULT_OK, resultIntent);
                }
            } else if (requestCode == 2) {
                // Handle new expense
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
                    // Forward the result to the calling activity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("new_expense", newExpense);
                    setResult(RESULT_OK, resultIntent);
                }
            }
        }
    }
}