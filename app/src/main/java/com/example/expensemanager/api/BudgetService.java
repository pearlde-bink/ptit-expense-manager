// BudgetService.java
package com.example.expensemanager.api;

import com.example.expensemanager.model.BudgetRequest;
import com.example.expensemanager.model.Budget;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BudgetService {
    @POST("/budgets")
    Call<Budget> createBudget(@Body BudgetRequest request);
}