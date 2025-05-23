//package com.example.expensemanager;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.expensemanager.adapter.EntryAdapter;
//import com.example.expensemanager.model.Entry;
//import com.example.expensemanager.model.Expense;
//import com.example.expensemanager.model.Income;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//public class Entries extends BaseActivity {
//
//    private RecyclerView entriesRecyclerView;
//    private EntryAdapter entryAdapter;
//    private List<Entry> entries;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_entries);
//
//        // Initialize views
//        ImageView backArrow = findViewById(R.id.back_arrow);
//        entriesRecyclerView = findViewById(R.id.entries_recycler_view);
//        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
//        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
//
//        // Initialize entries list
//        entries = new ArrayList<>();
////        populateSampleEntries();
//
//        // Set up RecyclerView
//        entryAdapter = new EntryAdapter(this, entries);
//        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        entriesRecyclerView.setAdapter(entryAdapter);
//
//        // Back Arrow
//        backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        // Set up Bottom Navigation
//        setupBottomNavigation();
//    }
//
//    @Override
//    protected int getSelectedNavItemId() {
//        return R.id.nav_home; // Highlight the "Entries" item (as a placeholder, adjust as needed)
//    }
//
//    @Override
//    protected Class<?> getFabTargetActivity() {
//        return Add.class; // FAB leads to AddActivity (for adding income/expense)
//    }
//
//    private void showAddEntryDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Add Entry");
//        builder.setItems(new String[]{"Add Income", "Add Expense"}, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0) {
//                    // Add Income
//                    Intent intent = new Intent(Entries.this, AddIncome.class);
//                    startActivityForResult(intent, 1);
//                } else {
//                    // Add Expense
//                    Intent intent = new Intent(Entries.this, AddExpense.class);
//                    startActivityForResult(intent, 2);
//                }
//            }
//        });
//        builder.show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == 1) {
//                // Handle new income
//                Income newIncome = (Income) data.getSerializableExtra("new_income");
//                if (newIncome != null) {
//                    entries.add(new Entry(
//                            newIncome.getDate(),
//                            newIncome.getTitle(),
//                            newIncome.getAmount(),
//                            newIncome.getCategory(),
//                            1.0, // Placeholder VAT
//                            "Google Pay", // Placeholder payment method
//                            false
//                    ));
//                    entryAdapter.notifyDataSetChanged();
//                }
//            } else if (requestCode == 2) {
//                // Handle new expense
//                Expense newExpense = (Expense) data.getSerializableExtra("new_expense");
//                if (newExpense != null) {
//                    entries.add(new Entry(
//                            newExpense.getDate(),
//                            newExpense.getTitle(),
//                            newExpense.getAmount(),
//                            newExpense.getCategory(),
//                            1.0, // Placeholder VAT
//                            "Google Pay", // Placeholder payment method
//                            true
//                    ));
//                    entryAdapter.notifyDataSetChanged();
//                }
//            }
//        }
//    }
//}