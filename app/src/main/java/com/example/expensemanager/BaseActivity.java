package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigation;
    protected FloatingActionButton fabAdd;

    // Abstract method to define the selected navigation item for each activity
    protected abstract int getSelectedNavItemId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view in the child class before calling setupBottomNavigation
    }

    protected void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
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
            } else if (itemId == R.id.nav_list) {
                targetActivity = SavingsActivity.class;
            } else if (itemId == R.id.nav_notifications) {
                targetActivity = NotificationActivity.class;
            } else if (itemId == R.id.nav_settings) {
                targetActivity = ReminderActivity.class;
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