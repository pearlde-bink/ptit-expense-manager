package com.example.expensemanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.GoalService;
import com.example.expensemanager.model.Goal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGoal extends BaseActivity {
    private static final String TAG = "AddGoal";

    private TextInputEditText goalTitleInput;
    private TextInputEditText amountInput;
    private MaterialAutoCompleteTextView contributionTypeInput;
    private TextInputEditText deadlineInput;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_goal);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        goalTitleInput = findViewById(R.id.goal_title_input);
        amountInput = findViewById(R.id.amount_input);
        contributionTypeInput = findViewById(R.id.contribution_type_input);
        deadlineInput = findViewById(R.id.deadline_input);
        MaterialButton addGoalButton = findViewById(R.id.btn_add_goal);

        selectedDate = Calendar.getInstance();

        backArrow.setOnClickListener(v -> finish());

        String[] contributionTypes = new String[]{"One-Time", "Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> contributionTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, contributionTypes);
        contributionTypeInput.setAdapter(contributionTypeAdapter);
        contributionTypeInput.setText("Yearly", false);

        deadlineInput.setOnClickListener(v -> showDatePickerDialog());

        addGoalButton.setOnClickListener(v -> {
            String goalTitle = goalTitleInput.getText().toString();
            String amountStr = amountInput.getText().toString();
            String contributionType = contributionTypeInput.getText().toString();
            String deadline = deadlineInput.getText().toString();

            if (goalTitle.isEmpty() || amountStr.isEmpty() || contributionType.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            String apiDeadline;
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                apiDeadline = outputFormat.format(inputFormat.parse(deadline));
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return;
            }
            GoalService goalService = ApiClient.getClient().create(GoalService.class);
            String accessToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");

            Goal goal = new Goal();
            goal.setTitle(goalTitle);
            goal.setTargetAmount(amount);
            goal.setCurrentAmount(0.0); // mặc định
            goal.setContributionType(contributionType);
            goal.setDeadline(apiDeadline);

            Call<Goal> call = goalService.createGoal(goal, "Bearer " + accessToken);
            call.enqueue(new Callback<Goal>() {
                @Override
                public void onResponse(Call<Goal> call, Response<Goal> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddGoal.this, "Goal added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddGoal.this, "Add failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Goal> call, Throwable t) {
                    Toast.makeText(AddGoal.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        setupBottomNavigation();

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
            deadlineInput.setText(dateFormat.format(selectedDate.getTime()));
        }, year, month, day);

        datePickerDialog.show();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_goal;
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return null;
    }
}
