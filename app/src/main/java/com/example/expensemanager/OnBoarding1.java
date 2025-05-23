package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OnBoarding1 extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000; // 2000ms = 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_boarding1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delay and switch to OnBoarding2
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(OnBoarding1.this, OnBoarding2.class);
            startActivity(intent);
            finish(); // Optional: prevent going back to OnBoarding1
        }, SPLASH_DELAY);
    }
}