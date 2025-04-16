package com.example.expensemanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.NotificationAdapter;
import com.example.expensemanager.model.Notification;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.notification_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NotificationAdapter(getSampleNotifications()));

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

        // Highlight the Notifications item
        bottomNavigation.setSelectedItemId(R.id.nav_notifications);

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
                Toast.makeText(NotificationActivity.this, "Add Button Clicked", Toast.LENGTH_SHORT).show();
                // Add your logic here (e.g., open a new screen to add an item)
            }
        });
    }

    // Sample data for the RecyclerView
    private List<Notification> getSampleNotifications() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification(R.drawable.food, "Food", "You just paid your food bill", "Just now"));
        notifications.add(new Notification(R.drawable.bill, "Reminder", "to pay your rent", "23 sec ago"));
        notifications.add(new Notification(R.drawable.vehicle, "Goal Achieved", "You just achieved your goal for new bike", "2 min ago"));
        notifications.add(new Notification(R.drawable.shopping, "Reminder", "You just set a new reminder shopping", "10 min ago"));
        notifications.add(new Notification(R.drawable.food, "Food", "You just paid your food bill", "45 min ago"));
        notifications.add(new Notification(R.drawable.bill, "Bill", "You just got a reminder for your bill pay", "1 hour ago"));
        notifications.add(new Notification(R.drawable.vehicle, "Uber", "You just paid your uber bill", "2 hour ago"));
        notifications.add(new Notification(R.drawable.entertainment, "Ticket", "You just paid for the movie ticket", "5 hour ago"));
        return notifications;
    }
}