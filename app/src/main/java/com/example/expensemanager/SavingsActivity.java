package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.adapter.GoalAdapter;
import com.example.expensemanager.model.Goal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SavingsActivity extends BaseActivity {

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
        populateSampleGoals();

        // Set up RecyclerView
        goalAdapter = new GoalAdapter(goals);
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        goalsRecyclerView.setAdapter(goalAdapter);

        // Update savings summary (placeholder)
        currentSavingsText.setText("$800");
        monthlyGoalText.setText("$200 / $500");

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

    private void populateSampleGoals() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.DECEMBER, 31);
        goals.add(new Goal("New Bike", 300, 600, "Monthly", calendar.getTime()));
        calendar.set(2025, Calendar.JUNE, 30);
        goals.add(new Goal("iPhone 15 Pro", 700, 1000, "One-Time", calendar.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Goal newGoal = (Goal) data.getSerializableExtra("new_goal");
            if (newGoal != null) {
                goalAdapter.addGoal(newGoal);
            }
        }
    }
}