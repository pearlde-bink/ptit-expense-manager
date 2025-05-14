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
import com.example.expensemanager.model.api.GoalOverviewResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavingsActivity extends BaseActivity implements GoalAdapter.GoalUpdateListener{
    private static final String TAG = "SavingsActivity";
    private TextView currentSavingsText;
    private TextView overviewGoalText;
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
        overviewGoalText = findViewById(R.id.overview_goal_text);
        goalsRecyclerView = findViewById(R.id.goals_list);

        // Set up Back Arrow
        backArrow.setOnClickListener(v -> finish());

        // Initialize goals list
        goals = new ArrayList<>();
//        populateSampleGoals();
        // Set up RecyclerView
        goalAdapter = new GoalAdapter(this, goals, this);
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        goalsRecyclerView.setAdapter(goalAdapter);

        // Fetch goals from API
        fetchGoals();
        fetchOverview();

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

    @Override
    protected void onResume() {
        super.onResume();
        fetchGoals(); // Refresh danh sách mỗi lần quay lại
        fetchOverview();
    }

    private void fetchOverview() {
        String token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");
        GoalService goalService = ApiClient.getClient().create(GoalService.class);

        goalService.getGoalsOverview("Bearer " + token).enqueue(new Callback<GoalOverviewResponse>() {
            @Override
            public void onResponse(Call<GoalOverviewResponse> call, Response<GoalOverviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GoalOverviewResponse overview = response.body();
                    // Update UI with the fetched data
                    String totalCurrentAmount = "$" + overview.getTotalCurrentAmount();
                    String overviewGoal = "$" + overview.getTotalCurrentAmount() + " / $" + overview.getTotalGoals();
                    currentSavingsText.setText(totalCurrentAmount);
                    overviewGoalText.setText(overviewGoal);
                } else {
                    Toast.makeText(SavingsActivity.this, "Failed to load overview", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GoalOverviewResponse> call, Throwable t) {
                Toast.makeText(SavingsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    @Override
    public void onGoalUpdated() {
        fetchOverview();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ADD_MONEY_REQUEST_CODE && resultCode == RESULT_OK) {
//            fetchGoals(); // Cập nhật danh sách goals
//            fetchOverview(); // Cập nhật tổng tiền đã tiết kiệm
//        }
//    }
}
