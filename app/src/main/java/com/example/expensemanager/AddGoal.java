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

import com.example.expensemanager.model.Goal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddGoal extends AppCompatActivity {
    private TextInputEditText goalTitleInput;
    private TextInputEditText amountInput;
    private MaterialAutoCompleteTextView contributionTypeInput;
    private TextInputEditText deadlineInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        goalTitleInput = findViewById(R.id.goal_title_input);
        amountInput = findViewById(R.id.amount_input);
        contributionTypeInput = findViewById(R.id.contribution_type_input);
        deadlineInput = findViewById(R.id.deadline_input);
        MaterialButton addGoalButton = findViewById(R.id.btn_add_goal);

        // Set up Back Arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up Contribution Type Dropdown
        String[] contributionTypes = new String[]{"One-Time", "Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> contributionTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, contributionTypes);
        contributionTypeInput.setAdapter(contributionTypeAdapter);
        contributionTypeInput.setText("Yearly", false); // Default value

        // Set up Date Picker
        deadlineInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up Add Goal Button
        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalTitle = goalTitleInput.getText().toString();
                String amountStr = amountInput.getText().toString();
                String contributionType = contributionTypeInput.getText().toString();
                String deadline = deadlineInput.getText().toString();

                if (goalTitle.isEmpty() || amountStr.isEmpty() || contributionType.isEmpty() || deadline.isEmpty()) {
                    Toast.makeText(AddGoal.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddGoal.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Goal object
                // Using a placeholder icon for now; you can add logic to select an icon
                Goal newGoal = new Goal(R.drawable.ic_star, goalTitle, 0.0, amount);

                // Pass the new Goal back to SavingsActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_goal", newGoal);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
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
            deadlineInput.setText(dateFormat.format(selectedDate.getTime()));
        }, year, month, day);

        datePickerDialog.show();
    }
}