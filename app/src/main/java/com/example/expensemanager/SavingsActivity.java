package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.GoalAdapter;
import com.example.expensemanager.model.Goal;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SavingsActivity extends AppCompatActivity {

    private GoalAdapter adapter;
    private List<Goal> goals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        // Initialize goals list
        goals = getSampleGoals();

        // Set up RecyclerView for Goals
        RecyclerView recyclerView = findViewById(R.id.goals_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(new com.example.expensemanager.adapter.GoalAdapter(getSampleGoals()));

        // Set up Back Arrow
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous screen
            }
        });
        // Set up Bottom Navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        // Highlight the Reminder item
        bottomNavigation.setSelectedItemId(R.id.nav_settings);

        // Handle BottomNavigationView item clicks
        bottomNavigation.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home) {
                Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show();
                return true;
            }else if(item.getItemId() == R.id.nav_list){
                Toast.makeText(this, "List Selected", Toast.LENGTH_SHORT).show();
                return true;
            }else if(item.getItemId() == R.id.nav_notifications){
                // Already on this screen
                return true;
            }else if(item.getItemId() == R.id.nav_settings){
                Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT).show();
                return true;
            } else{
                return false;
            }
        });


        // Handle FAB click
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SavingsActivity.this, "Add Button Clicked", Toast.LENGTH_SHORT).show();
                // Add your logic here (e.g., open a new screen to add a goal)
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Goal newGoal = (Goal) data.getSerializableExtra("new_goal");
            if (newGoal != null) {
                adapter.addGoal(newGoal);
            }
        }
    }
    private List<Goal> getSampleGoals() {
        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal(R.drawable.ic_bike, "New Bike", 300.0, 600.0));
        goals.add(new Goal(R.drawable.ic_phone, "iPhone 15 Pro", 700.0, 1000.0));// Add more items to ensure visibility
        goals.add(new Goal(R.drawable.ic_camera, "Laptop", 500.0, 1200.0));
        goals.add(new Goal(R.drawable.ic_compass, "Vacation", 200.0, 800.0));
        return goals;
    }
}