package com.example.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.AuthService;
import com.example.expensemanager.model.LoginRequest;
import com.example.expensemanager.model.LoginResponse;
import com.example.expensemanager.model.User;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

public class AuthenLogin extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authen_login);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.btn_login);
        TextView registerText = findViewById(R.id.register_link);

        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(AuthenLogin.this, Register_Activity.class);
            startActivity(intent);
        });

        AuthService authService = ApiClient.getClient().create(AuthService.class);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest loginRequest = new LoginRequest(username, password);

            authService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse == null) {
                            Toast.makeText(AuthenLogin.this, "Login failed: empty response", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String accessToken = loginResponse.getAccessToken();
                        String refreshToken = loginResponse.getRefreshToken();
                        User user = loginResponse.getUser();

                        saveLoginData(accessToken, refreshToken, user);
                      
                        // Chuyển màn hình
                        startActivity(new Intent(AuthenLogin.this, Overview.class));
                      
                        finish();
                    } else {
                        Toast.makeText(AuthenLogin.this, "Login failed!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(AuthenLogin.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void saveLoginData(String accessToken, String refreshToken, User user) {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("accessToken", accessToken);
        editor.putString("refreshToken", refreshToken);

        // Convert user object to JSON string
        String userJson = new Gson().toJson(user);
        editor.putString("user", userJson);

        editor.apply();
    }
}