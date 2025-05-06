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
import com.example.expensemanager.model.Goal;
import com.example.expensemanager.api.GoalService;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddGoal extends BaseActivity {
    private static final String TAG = "AddGoal";
    private static final String BASE_URL = "http://10.0.2.2:3000";
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
        try {
            setContentView(R.layout.activity_add_goal);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set content view", e);
            finish();
            return;
        }

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        goalTitleInput = findViewById(R.id.goal_title_input);
        amountInput = findViewById(R.id.amount_input);
        contributionTypeInput = findViewById(R.id.contribution_type_input);
        deadlineInput = findViewById(R.id.deadline_input);
        MaterialButton addGoalButton = findViewById(R.id.btn_add_goal);

        // Initialize calendar for date handling
        selectedDate = Calendar.getInstance();

        // Set up Back Arrow
        backArrow.setOnClickListener(v -> {
            Log.d(TAG, "Back arrow clicked");
            finish();
        });

        // Set up Contribution Type Dropdown
        String[] contributionTypes = new String[]{"One-Time", "Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> contributionTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, contributionTypes);
        contributionTypeInput.setAdapter(contributionTypeAdapter);
        contributionTypeInput.setText("Yearly", false); // Default value

        // Set up Date Picker
        deadlineInput.setOnClickListener(v -> showDatePickerDialog());

        // Set up Add Goal Button
        addGoalButton.setOnClickListener(v -> {
            Log.d(TAG, "Add Goal button clicked");
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
                Toast.makeText(AddGoal.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert deadline from "dd/MM/yyyy" to "yyyy-MM-dd" for API
            String apiDeadline;
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                apiDeadline = outputFormat.format(inputFormat.parse(deadline));
            } catch (ParseException e) {
                Toast.makeText(AddGoal.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return;
            }

            Goal newGoal = new Goal(1, goalTitle, 0.0, amount, contributionType, apiDeadline);
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra("new_goal", newGoal);
//            setResult(RESULT_OK, resultIntent);
//            Log.d(TAG, "Setting result and finishing");
//            finish();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GoalService goalService = retrofit.create(GoalService.class);

            goalService.createGoal(newGoal).enqueue(new Callback<Goal>() {
                @Override
                public void onResponse(Call<Goal> call, Response<Goal> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddGoal.this, "Goal created", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("new_goal", response.body());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(AddGoal.this, "Failed to create goal", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Goal> call, Throwable t) {
                    Toast.makeText(AddGoal.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failure: " + t.getMessage(), t);
                }
            });
        });

        // Set up bottom navigation
        setupBottomNavigation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart called");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume called");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause called");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop called");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy called");
//    }

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
        return R.id.nav_list;
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return null; // No FAB action in this screen
    }
}