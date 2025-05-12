package com.example.expensemanager.api;

import com.example.expensemanager.model.ImageUploadResponse;
import com.example.expensemanager.model.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {
    @PUT("user/{id}")
    Call<User> updateUser(
            @Path("id") String userId,
            @Body User updatedUser,
            @Header("Authorization") String token
    );

    @Multipart
    @POST("user/upload")
    Call<ImageUploadResponse> uploadAvatar(
            @Part MultipartBody.Part file,
            @Header("Authorization") String token
    );
}
