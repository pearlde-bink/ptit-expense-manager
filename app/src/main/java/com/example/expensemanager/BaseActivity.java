package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    protected BottomNavigationView bottomNavigation;
    protected FloatingActionButton fabAdd;

    // Abstract method to define the selected navigation item for each activity
    protected abstract int getSelectedNavItemId();

    // Abstract method to define the FAB target activity for each activity
    protected abstract Class<?> getFabTargetActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view in the child class before calling setupBottomNavigation
    }

    protected void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation == null) {
            Log.w(TAG, "BottomNavigationView not found in " + this.getClass().getSimpleName());
            return; // Skip setup if BottomNavigationView is not present
        }
        fabAdd = findViewById(R.id.fab_add);

        // Highlight the correct navigation item
        int selectedItemId = getSelectedNavItemId();
        if (selectedItemId != -1) {
            bottomNavigation.setSelectedItemId(selectedItemId);
        }

        // Handle navigation item clicks
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Class<?> targetActivity = null;

            if (itemId == R.id.nav_home) {
                targetActivity = Overview.class;
            } else if (itemId == R.id.nav_goal) {
                targetActivity = SavingsActivity.class;
            } else if (itemId == R.id.nav_statistic) {
                targetActivity = StatisticsActivity.class;
            } else if (itemId == R.id.nav_user) {
                targetActivity = User_Profile.class;
            }else if (itemId == R.id.nav_budget) {
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

        // Handle FAB click based on the target activity
//        fabAdd.setOnClickListener(v -> {
//            Class<?> targetActivity = getFabTargetActivity();
//            Log.d("BaseActivity", "FAB Target Activity: " + (targetActivity != null ? targetActivity.getSimpleName() : "null") + " from " + this.getClass().getSimpleName());
//            if (targetActivity != null) {
//                try {
//                    Intent intent = new Intent(this, targetActivity);
//                    startActivityForResult(intent, 1);
//                    Log.d("BaseActivity", "Started activity: " + targetActivity.getSimpleName());
//                } catch (Exception e) {
//                    Log.e("BaseActivity", "Failed to start activity: " + targetActivity.getSimpleName(), e);
//                }
//            } else {
//                Log.w("BaseActivity", "FAB target activity is null in " + this.getClass().getSimpleName());
//            }
//        });
        if (fabAdd != null) {
            if (getFabTargetActivity() != null) {
                fabAdd.setVisibility(View.VISIBLE);
                fabAdd.setOnClickListener(v -> {
                    Class<?> targetActivity = getFabTargetActivity();
                    Log.d(TAG, "FAB Target Activity: " + (targetActivity != null ? targetActivity.getSimpleName() : "null") + " from " + this.getClass().getSimpleName());
                    if (targetActivity != null) {
                        try {
                            Intent intent = new Intent(this, targetActivity);
                            startActivityForResult(intent, 1);
                            Log.d(TAG, "Started activity: " + targetActivity.getSimpleName());
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to start activity: " + targetActivity.getSimpleName(), e);
                        }
                    } else {
                        Log.w(TAG, "FAB target activity is null in " + this.getClass().getSimpleName());
                    }
                });
            } else {
                fabAdd.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
        }
    }
}