package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.ReminderAdapter;
import com.example.expensemanager.model.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReminderActivity extends BaseActivity {

    private RecyclerView remindersRecyclerView;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        remindersRecyclerView = findViewById(R.id.reminders_recycler_view);

        // Set up Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize reminders list
        reminders = new ArrayList<>();
        populateSampleReminders();

        // Set up RecyclerView
        reminderAdapter = new ReminderAdapter(this, reminders);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        remindersRecyclerView.setAdapter(reminderAdapter);

        // Set up bottom navigation
        setupBottomNavigation();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_settings;
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return AddReminder.class; // FAB leads to AddReminderActivity
    }

    private void populateSampleReminders() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(2024, Calendar.MAY, 26);
        reminders.add(new Reminder("Bill Payment", 200, calendar.getTime(), "Monthly", true));
        reminders.add(new Reminder("Car Loan", 2000, calendar.getTime(), "Monthly", true));
        reminders.add(new Reminder("iPhone 15 Pro", 1000, calendar.getTime(), "One-Time", true));
        calendar.set(2024, Calendar.SEPTEMBER, 11);
        reminders.add(new Reminder("New Bike", 500, calendar.getTime(), "One-Time", false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Reminder newReminder = (Reminder) data.getSerializableExtra("new_reminder");
            if (newReminder != null) {
                reminders.add(newReminder);
                reminderAdapter.notifyDataSetChanged();
            }
        }
    }
}