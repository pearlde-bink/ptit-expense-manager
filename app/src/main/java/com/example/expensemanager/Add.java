package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.EntryAdapter;
import com.example.expensemanager.model.Entry;
import com.example.expensemanager.model.Expense;
import com.example.expensemanager.model.Income;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Add extends AppCompatActivity {

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
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

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
        bottomNavigation.setSelectedItemId(R.id.nav_list); // Highlight the Entries item

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_list) {
                // Already on this screen
                return true;
            } else if (item.getItemId() == R.id.nav_notifications) {
                Toast.makeText(this, "Notifications Selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });

        // Handle FAB click
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since we already have buttons for adding income/expense, we can make the FAB redundant
                // Alternatively, we can show the same dialog as in EntriesActivity
                Toast.makeText(Add.this, "Use the buttons above to add an entry", Toast.LENGTH_SHORT).show();
            }
        });
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
                }
            }
        }
    }
}