package com.example.expensemanager.api;

import com.example.expensemanager.model.Budget;
import com.example.expensemanager.model.BudgetRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BudgetService {
    @POST("budgets")
    Call<Budget> createBudget(@Header("Authorization") String authHeader, @Body BudgetRequest request);

    @GET("budgets")
    Call<List<Budget>> getBudgets(@Header("Authorization") String authHeader);

    @GET("budgets/{id}")
    Call<Budget> getBudget(@Header("Authorization") String authHeader, @Path("id") int id);

    @PUT("budgets/{id}")
    Call<Budget> updateBudget(@Header("Authorization") String authHeader, @Path("id") int id, @Body Budget budget);

    @DELETE("budgets/{id}")
    Call<Void> deleteBudget(@Header("Authorization") String authHeader, @Path("id") int id);
}