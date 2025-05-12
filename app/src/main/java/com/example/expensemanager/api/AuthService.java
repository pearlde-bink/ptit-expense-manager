package com.example.expensemanager.api;
import com.example.expensemanager.model.LoginRequest;
import com.example.expensemanager.model.LoginResponse;
import com.example.expensemanager.model.RegisterRequest;
import com.example.expensemanager.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
}
