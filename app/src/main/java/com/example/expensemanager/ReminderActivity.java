package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.ReminderAdapter;
import com.example.expensemanager.model.Reminder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ReminderActivity extends AppCompatActivity {
    private ReminderAdapter adapter;
    private List<Reminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminder);

        // Initialize reminders list
        reminders = getSampleReminders();

//        Setup RecycleView
        RecyclerView recyclerView = findViewById(R.id.reminder_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new ReminderAdapter(getSampleReminders()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        adapter = new ReminderAdapter(this, reminders);
        recyclerView.setAdapter(adapter);

        // Set up Back Arrow
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous screen
            }
        });

        // Set up Bottom Navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        // Highlight the Reminder item
        bottomNavigation.setSelectedItemId(R.id.nav_settings);


        // Handle BottomNavigationView item clicks
        bottomNavigation.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home) {
                Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show();
                return true;
            }else if(item.getItemId() == R.id.nav_list){
                Toast.makeText(this, "List Selected", Toast.LENGTH_SHORT).show();
                return true;
            }else if(item.getItemId() == R.id.nav_notifications){
                // Already on this screen
                return true;
            }else if(item.getItemId() == R.id.nav_settings){
                Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT).show();
                return true;
            } else{
                return false;
            }
        });

        // Handle FAB click
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReminderActivity.this, "Add Button Clicked", Toast.LENGTH_SHORT).show();
                // Add your logic here (e.g., open a new screen to add an item)
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Reminder updatedReminder = (Reminder) data.getSerializableExtra("updated_reminder");
            int position = data.getIntExtra("position", -1);
            if (position != -1 && updatedReminder != null) {
                adapter.updateReminder(position, updatedReminder);
            }
        }
    }

    // Sample data for the RecyclerView
    private List<Reminder> getSampleReminders() {
        List<Reminder> reminders = new ArrayList<>();
        reminders.add(new Reminder("24 May 2024", "Bill Payment", false, 360.0, "24 June 2024"));
        reminders.add(new Reminder("15 April 2024", "Rent Payment", true, 1200.0, "15 May 2024"));
        reminders.add(new Reminder("01 June 2024", "Grocery Shopping", false, 150.0, "01 July 2024"));
        reminders.add(new Reminder("10 May 2024", "Car Insurance", true, 450.0, "10 June 2024"));
        reminders.add(new Reminder("20 June 2024", "Internet Bill", false, 60.0, "20 July 2024"));
        reminders.add(new Reminder("05 May 2024", "Credit Card Payment", true, 800.0, "05 June 2024"));
        reminders.add(new Reminder("30 April 2024", "Gym Membership", false, 50.0, "30 May 2024"));
        reminders.add(new Reminder("12 June 2024", "Phone Bill", true, 45.0, "12 July 2024"));
        reminders.add(new Reminder("25 May 2024", "Electricity Bill", false, 200.0, "25 June 2024"));
        reminders.add(new Reminder("18 April 2024", "Water Bill", true, 30.0, "18 May 2024"));
        return reminders;
    }
}