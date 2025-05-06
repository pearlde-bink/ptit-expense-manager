// BudgetService.java
package com.example.expensemanager.api;

import com.example.expensemanager.model.BudgetRequest;
import com.example.expensemanager.model.Budget;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BudgetService {
    @POST("/budgets")
    Call<Budget> createBudget(@Body BudgetRequest request);

    @GET("budget")
    Call<List<Budget>> getBudgets();

    @GET("budget/{id}")
    Call<Budget> getBudget(@Path("id") int id);

    @PUT("budget/{id}")
        Call<Budget> updateBudget(@Path("id") int id, @Body Budget budget);

    @DELETE("budget/{id}")
    Call<Void> deleteBudget(@Path("id") int id);
}