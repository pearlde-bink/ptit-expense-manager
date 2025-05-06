package com.example.expensemanager.api;

import com.example.expensemanager.model.Goal;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GoalService {
    @GET("goal")
    Call<List<Goal>> getGoals();

    @POST("goal")
    Call<Goal> createGoal(@Body Goal goal);

    @GET("goal/{id}")
    Call<Goal> getGoal(@Path("id") int id);

    @PUT("goal/{id}")
    Call<Goal> updateGoal(@Path("id") int id, @Body Goal goal);

    @DELETE("goal/{id}")
    Call<Void> deleteGoal(@Path("id") int id);
}