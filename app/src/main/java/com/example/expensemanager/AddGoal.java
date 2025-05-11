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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddGoal extends BaseActivity {
    private static final String TAG = "AddGoal";
    private static final String BASE_URL = "http://10.0.2.2:3000";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
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

            // Tạo JSON payload //hien tai mac dinh userid la 1
            String jsonBody = String.format(
                    "{\"title\":\"%s\",\"currentAmount\":0.0,\"targetAmount\":%f,\"contributionType\":\"%s\",\"deadline\":\"%s\",\"userId\":1}",
                    goalTitle, amount, contributionType, apiDeadline
            );

            // Gửi yêu cầu POST tới backend
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/goal")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddGoal.this, "Failed to add goal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "API call failed", e);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddGoal.this, "Goal added successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Goal added successfully");
                            finish(); // Quay lại màn hình trước
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(AddGoal.this, "Failed to add goal: " + response.message(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                        });
                    }
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

//package com.example.expensemanager;
//
//import android.app.DatePickerDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.Toast;
//import androidx.activity.EdgeToEdge;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import com.example.expensemanager.model.Goal;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.textfield.MaterialAutoCompleteTextView;
//import com.google.android.material.textfield.TextInputEditText;
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Locale;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class AddGoal extends BaseActivity {
//    private static final String TAG = "AddGoal";
//    private static final String BASE_URL = "http://10.0.2.2:3000"; // Đảm bảo port khớp với backend NestJS
//    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
//
//    private TextInputEditText goalTitleInput;
//    private TextInputEditText amountInput;
//    private MaterialAutoCompleteTextView contributionTypeInput;
//    private TextInputEditText deadlineInput;
//    private Calendar selectedDate;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate called");
//        EdgeToEdge.enable(this);
//        try {
//            setContentView(R.layout.activity_add_goal);
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to set content view", e);
//            finish();
//            return;
//        }
//
//        // Initialize views
//        ImageView backArrow = findViewById(R.id.back_arrow);
//        goalTitleInput = findViewById(R.id.goal_title_input);
//        amountInput = findViewById(R.id.amount_input);
//        contributionTypeInput = findViewById(R.id.contribution_type_input);
//        deadlineInput = findViewById(R.id.deadline_input);
//        MaterialButton addGoalButton = findViewById(R.id.btn_add_goal);
//
//        // Initialize calendar for date handling
//        selectedDate = Calendar.getInstance();
//
//        // Set up Back Arrow
//        backArrow.setOnClickListener(v -> {
//            Log.d(TAG, "Back arrow clicked");
//            finish();
//        });
//
//        // Set up Contribution Type Dropdown
//        String[] contributionTypes = new String[]{"One-Time", "Daily", "Weekly", "Monthly", "Yearly"};
//        ArrayAdapter<String> contributionTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, contributionTypes);
//        contributionTypeInput.setAdapter(contributionTypeAdapter);
//        contributionTypeInput.setText("Yearly", false); // Default value
//
//        // Set up Date Picker
//        deadlineInput.setOnClickListener(v -> showDatePickerDialog());
//
//        // Set up Add Goal Button
//        addGoalButton.setOnClickListener(v -> {
//            Log.d(TAG, "Add Goal button clicked");
//            String goalTitle = goalTitleInput.getText().toString().trim();
//            String amountStr = amountInput.getText().toString().trim();
//            String contributionType = contributionTypeInput.getText().toString().trim();
//            String deadline = deadlineInput.getText().toString().trim();
//
//            if (goalTitle.isEmpty() || amountStr.isEmpty() || contributionType.isEmpty() || deadline.isEmpty()) {
//                Toast.makeText(AddGoal.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            double amount;
//            try {
//                amount = Double.parseDouble(amountStr);
//            } catch (NumberFormatException e) {
//                Toast.makeText(AddGoal.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Convert deadline from "dd/MM/yyyy" to "yyyy-MM-dd" for API
//            String apiDeadline;
//            try {
//                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                apiDeadline = outputFormat.format(inputFormat.parse(deadline));
//            } catch (ParseException e) {
//                Toast.makeText(AddGoal.this, "Invalid date format", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Tạo JSON payload
//            String jsonBody = String.format(
//                    "{\"title\":\"%s\",\"currentAmount\":0.0,\"targetAmount\":%f,\"contributionType\":\"%s\",\"deadline\":\"%s\",\"userId\":1}",
//                    goalTitle, amount, contributionType, apiDeadline
//            );
//
//            // Gửi yêu cầu POST tới backend
//            OkHttpClient client = new OkHttpClient();
//            RequestBody body = RequestBody.create(jsonBody, JSON);
//            Request request = new Request.Builder()
//                    .url(BASE_URL + "/goal")
//                    .post(body)
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    runOnUiThread(() -> {
//                        Toast.makeText(AddGoal.this, "Failed to add goal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "API call failed", e);
//                    });
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        runOnUiThread(() -> {
//                            Toast.makeText(AddGoal.this, "Goal added successfully", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "Goal added successfully");
//                            finish(); // Quay lại màn hình trước
//                        });
//                    } else {
//                        runOnUiThread(() -> {
//                            Toast.makeText(AddGoal.this, "Failed to add goal: " + response.message(), Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Error: " + response.code() + " - " + response.message());
//                        });
//                    }
//                }
//            });
//        });
//
//        // Set up bottom navigation
//        setupBottomNavigation();
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//    private void showDatePickerDialog() {
//        int year = selectedDate.get(Calendar.YEAR);
//        int month = selectedDate.get(Calendar.MONTH);
//        int day = selectedDate.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
//            selectedDate.set(selectedYear, selectedMonth, selectedDay);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//            deadlineInput.setText(dateFormat.format(selectedDate.getTime()));
//        }, year, month, day);
//
//        datePickerDialog.show();
//    }
//
//    @Override
//    protected int getSelectedNavItemId() {
//        return R.id.nav_list;
//    }
//
//    @Override
//    protected Class<?> getFabTargetActivity() {
//        return null; // No FAB action in this screen
//    }
//}