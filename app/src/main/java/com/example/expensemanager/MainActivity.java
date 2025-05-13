package com.example.expensemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", null);

        if (accessToken != null && !accessToken.isEmpty()) {
            // Đã login → chuyển sang màn hình chính
            startActivity(new Intent(MainActivity.this, Overview.class));
        } else {
            // Chưa login → chuyển sang màn hình đăng nhập
            startActivity(new Intent(MainActivity.this, AuthenLogin.class));
        }

        finish(); // đóng MainActivity để không quay lại splash
    }
}