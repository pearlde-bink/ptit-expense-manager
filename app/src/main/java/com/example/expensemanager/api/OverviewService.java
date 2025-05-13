package com.example.expensemanager.api;

import com.example.expensemanager.model.Entry;
import com.example.expensemanager.model.OverviewResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface OverviewService {
    @GET("budgets/overview")
    Call<OverviewResponse> getOverviewData(@Header("Authorization") String token);

    @GET("budgets/entries")
    Call<List<Entry>> getLastestEntries(@Header("Authorization") String token);
}