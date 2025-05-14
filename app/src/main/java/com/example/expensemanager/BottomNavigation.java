package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomNavigation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottom_navigation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    protected BottomNavigationView bottomNavigation;
    protected FloatingActionButton fabAdd;

    // Abstract method to define the selected navigation item for each activity
    protected int getSelectedNavItemId() {
        return 0;
    }

    protected void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabAdd = findViewById(R.id.fab_add);

        // Highlight the correct navigation item
        bottomNavigation.setSelectedItemId(getSelectedNavItemId());

        // Handle navigation item clicks
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Class<?> targetActivity = null;

            if (itemId == R.id.nav_home) {
                targetActivity = Overview.class;
            } else if (itemId == R.id.nav_goal) {
                targetActivity = AddGoal.class;
            } else if (itemId == R.id.nav_statistic) {
             targetActivity = StatisticsActivity.class;
            } else if (itemId == R.id.nav_user) {
             targetActivity = User_Profile.class;
            } else if (itemId == R.id.nav_budget) {
                targetActivity = BudgetActivity.class;
            }

            // If we're already on the target activity, do nothing
            if (targetActivity != null && !this.getClass().equals(targetActivity)) {
                Intent intent = new Intent(this, targetActivity);
                // Clear the back stack to prevent stacking activities
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Handle FAB click
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, Add.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Toast.makeText(this, "Entry added", Toast.LENGTH_SHORT).show();
        }
    }
}