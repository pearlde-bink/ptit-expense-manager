package com.example.expensemanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.expensemanager.model.Reminder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddReminder extends BaseActivity {

    private TextInputEditText titleEditText, amountEditText, dueDateEditText;
    private MaterialButton setReminderButton;
    private AutoCompleteTextView frequencyEditText;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        titleEditText = findViewById(R.id.reminder_title);
        amountEditText = findViewById(R.id.reminder_amount);
        frequencyEditText = findViewById(R.id.reminder_frequency);
        dueDateEditText = findViewById(R.id.reminder_due_date);
        setReminderButton = findViewById(R.id.btn_set_reminder);

        // Set up Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Set up calendar
        calendar = Calendar.getInstance();

        // Set up frequency dropdown
        String[] frequencies = {"Select One", "Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, frequencies);
        frequencyEditText.setAdapter(frequencyAdapter);
        frequencyEditText.setOnClickListener(v -> frequencyEditText.showDropDown());

        // Set up due date picker
        dueDateEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dueDateEditText.setText(dateFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Set reminder button
        setReminderButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String amountStr = amountEditText.getText().toString();
            String frequency = frequencyEditText.getText().toString();
            Date dueDate = calendar.getTime();

            if (title.isEmpty() || amountStr.isEmpty() || frequency.equals("Select One") || dueDateEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            // Default state to true (active) for new reminders
            Reminder newReminder = new Reminder(title, amount, dueDate, frequency, true);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_reminder", newReminder);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Set up bottom navigation
        setupBottomNavigation();
    }

    @Override
    protected int getSelectedNavItemId() {
        return -1; // No bottom navigation item highlighted
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return null; // No FAB action in this intermediate screen
    }
}