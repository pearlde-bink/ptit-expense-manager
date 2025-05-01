package com.example.expensemanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.expensemanager.model.Reminder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SetReminder extends AppCompatActivity {
    private TextInputEditText amountInput;
    private MaterialAutoCompleteTextView billTypeInput;
    private MaterialAutoCompleteTextView frequencyInput;
    private TextInputEditText dateInput;
    private SwitchMaterial stateSwitch;
    private Reminder reminder;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_reminder);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        amountInput = findViewById(R.id.amount_input);
        billTypeInput = findViewById(R.id.bill_type_input);
        frequencyInput = findViewById(R.id.frequency_input);
        dateInput = findViewById(R.id.date_input);
        stateSwitch = findViewById(R.id.state_switch);
        MaterialButton setReminderButton = findViewById(R.id.btn_set_reminder);

        // Initialize calendar for date handling
        selectedDate = Calendar.getInstance();

        // Check if we're editing an existing reminder
        reminder = (Reminder) getIntent().getSerializableExtra("reminder");
        if (reminder != null) {
            billTypeInput.setText(reminder.getTitle());
            amountInput.setText(String.valueOf(reminder.getAmount()));
            frequencyInput.setText(reminder.getFrequency(), false);
            stateSwitch.setChecked(reminder.getState());

            // Format the due date and set it
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateInput.setText(dateFormat.format(reminder.getDueDate()));
            selectedDate.setTime(reminder.getDueDate());
        }

        // Set up Back Arrow
        backArrow.setOnClickListener(v -> finish());

        // Set up Bill Type Dropdown
        String[] billTypes = new String[]{"Car Loan", "Rent", "Grocery", "Insurance", "Internet Bill", "Credit Card", "Gym Membership", "Phone Bill", "Electricity Bill", "Water Bill"};
        ArrayAdapter<String> billTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, billTypes);
        billTypeInput.setAdapter(billTypeAdapter);

        // Set up Frequency Dropdown
        String[] frequencies = new String[]{"One-Time", "Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, frequencies);
        frequencyInput.setAdapter(frequencyAdapter);
        if (reminder == null) {
            frequencyInput.setText("One-Time", false); // Default value for new reminder
        }

        // Set up Date Picker
        dateInput.setOnClickListener(v -> showDatePickerDialog());

        // Set up Set Reminder Button
        setReminderButton.setOnClickListener(v -> {
            String billType = billTypeInput.getText().toString();
            String amountStr = amountInput.getText().toString();
            String frequency = frequencyInput.getText().toString();
            String date = dateInput.getText().toString();
            boolean state = stateSwitch.isChecked();

            if (billType.isEmpty() || amountStr.isEmpty() || frequency.isEmpty() || date.isEmpty()) {
                Toast.makeText(SetReminder.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(SetReminder.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create or update the Reminder object
            Reminder updatedReminder = new Reminder(billType, amount, selectedDate.getTime(), frequency, state);

            // Pass the updated Reminder back to the previous activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updated_reminder", updatedReminder);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDatePickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedDate.set(selectedYear, selectedMonth, selectedDay);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateInput.setText(dateFormat.format(selectedDate.getTime()));
        }, year, month, day);

        datePickerDialog.show();
    }
}