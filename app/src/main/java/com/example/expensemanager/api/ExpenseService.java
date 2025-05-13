package com.example.expensemanager.api;

import com.example.expensemanager.model.Expense;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ExpenseService {

    @POST("expenses")
    Call<Expense> createExpense(@Body Expense expense, @Header("Authorization") String token);

    @GET("expenses")
    Call<List<Expense>> getExpenses(@Header("Authorization") String token);

    @GET("expenses/{id}")
    Call<Expense> getExpense(@Path("id") int id, @Header("Authorization") String token);

    @PATCH("expenses/{id}")
    Call<Expense> updateExpense(@Path("id") int id, @Body Expense expense, @Header("Authorization") String token);

    @DELETE("expenses/{id}")
    Call<Void> deleteExpense(@Path("id") int id, @Header("Authorization") String token);
}