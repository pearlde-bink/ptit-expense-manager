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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SetReminder extends AppCompatActivity {
    private TextInputEditText amountInput;
    private MaterialAutoCompleteTextView billTypeInput;
    private MaterialAutoCompleteTextView frequencyInput;
    private TextInputEditText dateInput;
    private Reminder reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_reminder);

        ImageView backArrow = findViewById(R.id.back_arrow);
        amountInput = findViewById(R.id.amount_input);
        billTypeInput = findViewById(R.id.bill_type_input);
        frequencyInput = findViewById(R.id.frequency_input);
        dateInput = findViewById(R.id.date_input);
        MaterialButton setReminderButton = findViewById(R.id.btn_set_reminder);

        reminder = (Reminder) getIntent().getSerializableExtra("reminder");
        if(reminder != null){
            billTypeInput.setText(reminder.getTitle());
            amountInput.setText(String.valueOf(reminder.getAmount()));
            dateInput.setText(reminder.getEndDate());
            //frequency can be left default
        }

        // Set up Back Arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up Bill Type Dropdown
        String[] billTypes = new String[]{"Car Loan", "Rent", "Grocery", "Insurance", "Internet Bill", "Credit Card", "Gym Membership", "Phone Bill", "Electricity Bill", "Water Bill"};
        ArrayAdapter<String> billTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, billTypes);
        billTypeInput.setAdapter(billTypeAdapter);

        // Set up Frequency Dropdown
        String[] frequencies = new String[]{"One-Time", "Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, frequencies);
        frequencyInput.setAdapter(frequencyAdapter);
        frequencyInput.setText("One-Time", false); // Default value

        // Set up Date Picker
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up Set Reminder Button
        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String billType = billTypeInput.getText().toString();
                String amountStr = amountInput.getText().toString();
                String frequency = frequencyInput.getText().toString();
                String date = dateInput.getText().toString();

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

                // Create a new Reminder object with the updated data
                Reminder updatedReminder = new Reminder(reminder != null ? reminder.getStartDate() : date, billType, reminder != null ? reminder.isState() : false, amount, date);

                // Pass the updated Reminder back to the previous activity (if needed)
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updated_reminder", updatedReminder);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedYear, selectedMonth, selectedDay);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateInput.setText(dateFormat.format(selectedDate.getTime()));
        }, year, month, day);

        datePickerDialog.show();
    }
}