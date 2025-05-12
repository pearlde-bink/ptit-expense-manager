package com.example.expensemanager.api;

import com.example.expensemanager.model.Expense;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ExpenseService {
    @GET("expense")
    Call<List<Expense>> getExpenses(@Header("Authorization") String authHeader);
}
