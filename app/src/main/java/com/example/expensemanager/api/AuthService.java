package com.example.expensemanager.api;
import com.example.expensemanager.model.LoginRequest;
import com.example.expensemanager.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
