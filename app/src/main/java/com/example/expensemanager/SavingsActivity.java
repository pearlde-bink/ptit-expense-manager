package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.adapter.GoalAdapter;
import com.example.expensemanager.api.GoalService;
import com.example.expensemanager.model.Goal;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SavingsActivity extends BaseActivity {
    private static final String TAG = "SavingsActivity";
    private final String BASE_URL = "http://10.0.2.2:3000";

    private TextView currentSavingsText;
    private TextView monthlyGoalText;
    private RecyclerView goalsRecyclerView;
    private GoalAdapter goalAdapter;
    private List<Goal> goals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_savings);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        currentSavingsText = findViewById(R.id.current_savings);
        monthlyGoalText = findViewById(R.id.monthly_goal_text);
        goalsRecyclerView = findViewById(R.id.goals_list);

        // Set up Back Arrow
        backArrow.setOnClickListener(v -> finish());

        // Initialize goals list
        goals = new ArrayList<>();
//        populateSampleGoals();
        // Set up RecyclerView
        goalAdapter = new GoalAdapter(goals);
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        goalsRecyclerView.setAdapter(goalAdapter);

        // Update savings summary (placeholder)
        currentSavingsText.setText("$800");
        monthlyGoalText.setText("$200 / $500");

        // Fetch goals from API
        fetchGoals();

        // Set up bottom navigation
        setupBottomNavigation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_list;
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return AddGoal.class; // FAB leads to AddGoal
    }
    private void fetchGoals() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + "/goal")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(SavingsActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API call failed", e);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "JSON Response: " + jsonResponse);
                    try {
                        // Sử dụng GsonBuilder để bỏ qua các field không khớp
                        Gson gson = new GsonBuilder().create();
                        List<Goal> newGoals = gson.fromJson(jsonResponse, new TypeToken<List<Goal>>(){}.getType());
                        runOnUiThread(() -> {
                            goals.clear();
                            goals.addAll(newGoals);
                            goalAdapter.setGoals(goals);
                            Toast.makeText(SavingsActivity.this, "Goals loaded", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Goals fetched successfully");
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(SavingsActivity.this, "Failed to parse goals: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Parse error", e);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SavingsActivity.this, "Failed to load goals: " + response.message(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Goal newGoal = (Goal) data.getSerializableExtra("new_goal");
//            if (newGoal != null) {
//                goalAdapter.addGoal(newGoal);
//            }
            if (newGoal != null) {
                // Add the new goal to the list and refresh the adapter
                goals.add(newGoal);
                goalAdapter.setGoals(goals);
                fetchGoals(); // Refresh to ensure consistency with server
            }
        }
    }
}
