package com.example.expensemanager.api;

import com.example.expensemanager.model.Income;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IncomeService {

    @POST("income")
    Call<Income> createIncome(@Body Income income, @Header("Authorization") String token);

    @GET("income")
    Call<List<Income>> getIncomes(@Header("Authorization") String token);

    @GET("income/{id}")
    Call<Income> getIncome(@Path("id") int id, @Header("Authorization") String token);

    @PUT("income/{id}")
    Call<Income> updateIncome(@Path("id") int id, @Body Income income, @Header("Authorization") String token);

    @DELETE("income/{id}")
    Call<Void> deleteIncome(@Path("id") int id, @Header("Authorization") String token);
}