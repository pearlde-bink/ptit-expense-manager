package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.GoalService;
import com.example.expensemanager.model.Goal;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavingsActivity extends BaseActivity {
    private static final String TAG = "SavingsActivity";
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
        return R.id.nav_goal;
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return AddGoal.class; // FAB leads to AddGoal
    }
    private void fetchGoals() {
        String token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");
        GoalService goalService = ApiClient.getClient().create(GoalService.class);

        goalService.getGoals("Bearer " + token).enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    goals.clear();
                    goals.addAll(response.body());
                    goalAdapter.setGoals(goals);
                    Toast.makeText(SavingsActivity.this, "Goals loaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SavingsActivity.this, "Failed to load goals", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Goal>> call, Throwable t) {
                Toast.makeText(SavingsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == 1) {
//            Goal newGoal = (Goal) data.getSerializableExtra("new_goal");
////            if (newGoal != null) {
////                goalAdapter.addGoal(newGoal);
////            }
//            if (newGoal != null) {
//                // Add the new goal to the list and refresh the adapter
//                goals.add(newGoal);
//                goalAdapter.setGoals(goals);
//                fetchGoals(); // Refresh to ensure consistency with server
//            }
//        }
//    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchGoals(); // Refresh danh sách mỗi lần quay lại
    }
}
