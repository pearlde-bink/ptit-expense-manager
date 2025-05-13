package com.example.expensemanager.api;

import com.example.expensemanager.model.Category;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CategoryService {
    @GET("categories")
    Call<List<Category>> getCategories(@Header("Authorization") String token);

    @POST("categories")
    Call<Category> createCategory(@Body Category category, @Header("Authorization") String token);
}