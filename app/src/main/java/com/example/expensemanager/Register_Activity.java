package com.example.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.AuthService;
import com.example.expensemanager.api.UserService;
import com.example.expensemanager.model.RegisterRequest;
import com.example.expensemanager.model.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register_Activity extends AppCompatActivity {

    private EditText usernameInput, fullNameInput, emailInput, phoneInput, passwordInput;
    private MaterialButton btnRegister;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // layout bạn tạo XML ở bước trước

        // Ánh xạ view
        usernameInput = findViewById(R.id.username_input);
        fullNameInput = findViewById(R.id.fullname_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        passwordInput = findViewById(R.id.password_input);
        btnRegister = findViewById(R.id.btn_register);
        loginLink = findViewById(R.id.login_link);

        // Đăng ký
        btnRegister.setOnClickListener(v -> handleRegister());

        // Chuyển về login
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(Register_Activity.this, AuthenLogin.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleRegister() {
        String username = usernameInput.getText().toString().trim();
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate đơn giản
        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(fullName, email, password, username, phone);

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        authService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();

                    // Lưu vào SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("accessToken", registerResponse.getAccessToken());
                    editor.putString("refreshToken", registerResponse.getRefreshToken());
                    editor.putString("user", new Gson().toJson(registerResponse.getUser()));
                    editor.apply();

                    Toast.makeText(Register_Activity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình chính (Overview)
                    Intent intent = new Intent(Register_Activity.this, Overview.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(Register_Activity.this, "Đăng ký thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(Register_Activity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}