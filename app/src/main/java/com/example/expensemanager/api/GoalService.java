package com.example.expensemanager.api;

import com.example.expensemanager.model.AddToGoalRequest;
import com.example.expensemanager.model.Goal;
import com.example.expensemanager.model.api.GoalOverviewResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GoalService {

    @GET("goal")
    Call<List<Goal>> getGoals(@Header("Authorization") String token);

    @GET("goal/overview")
    Call<GoalOverviewResponse> getGoalsOverview(@Header("Authorization") String token);

    @POST("goal/add-to-goal")
    Call<Goal> createGoal(@Body AddToGoalRequest goal, @Header("Authorization") String token);

    @POST("goal")
    Call<Goal> createGoal(@Body Goal goal, @Header("Authorization") String token);

    @GET("goal/{id}")
    Call<Goal> getGoal(@Path("id") int id, @Header("Authorization") String token);

    @PUT("goal/{id}")
    Call<Goal> updateGoal(@Path("id") int id, @Body Goal goal, @Header("Authorization") String token);

    @DELETE("goal/{id}")
    Call<Void> deleteGoal(@Path("id") int id, @Header("Authorization") String token);
}