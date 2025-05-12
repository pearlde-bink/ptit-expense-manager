package com.example.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.UserService;
import com.example.expensemanager.model.FileUtils;
import com.example.expensemanager.model.ImageUploadResponse;
import com.example.expensemanager.model.User;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User_Profile extends AppCompatActivity {

    private User currentUser;
    private SharedPreferences sharedPref;
    private Gson gson = new Gson();

    // View hiển thị
    private TextView tvFullName, tvUsername, tvPhone, tvEmail;

    // Form chỉnh sửa
    private LinearLayout editForm;
    private EditText etFullName, etPhone, etEmail;
    private Button btnSave;
    private ImageButton btnEdit;
    private ImageView dialogAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Ánh xạ view
        tvFullName = findViewById(R.id.tvFullName);
        tvUsername = findViewById(R.id.tvUsername);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        editForm = findViewById(R.id.editForm);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        btnEdit = findViewById(R.id.btnEdit);

        sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        loadUserFromPrefs();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(User_Profile.this, Overview.class); // hoặc tên Activity bạn muốn về
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // quay về không giữ lại màn hiện tại
            startActivity(intent);
            finish(); // đóng User_Profile
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // 1. Xoá token, user
            sharedPref.edit().clear().apply();

            // 2. Chuyển về màn hình đăng nhập
            Intent intent = new Intent(User_Profile.this, AuthenLogin.class); // đổi LoginActivity nếu bạn dùng tên khác
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // xoá back stack
            startActivity(intent);

            Toast.makeText(User_Profile.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        });

        // 1. Bắt sự kiện nút Edit
        btnEdit.setOnClickListener(v -> {
            if (currentUser == null) return;

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);

            ImageView dialogAvatar = dialogView.findViewById(R.id.dialogAvatar);
            EditText dialogFullName = dialogView.findViewById(R.id.dialogFullName);
            EditText dialogPhone = dialogView.findViewById(R.id.dialogPhone);
            EditText dialogEmail = dialogView.findViewById(R.id.dialogEmail);
            this.dialogAvatar = dialogAvatar;

            // Gán dữ liệu hiện tại
            dialogFullName.setText(currentUser.getFullName());
            dialogPhone.setText(currentUser.getPhone());
            dialogEmail.setText(currentUser.getEmail());

            // Mở chọn ảnh khi click avatar
            dialogAvatar.setOnClickListener(view -> {
                // mở gallery
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1234); // requestCode = 1234
            });

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Chỉnh sửa thông tin")
                    .setView(dialogView)
                    .setPositiveButton("Lưu", (d, which) -> {
                        // Gọi API cập nhật user
                        String newName = dialogFullName.getText().toString();
                        String newPhone = dialogPhone.getText().toString();
                        String newEmail = dialogEmail.getText().toString();

                        // Tạo user mới
                        User updatedUser = new User();
                        updatedUser.setId(currentUser.getId());
                        updatedUser.setFullName(newName);
                        updatedUser.setPhone(newPhone);
                        updatedUser.setEmail(newEmail);
                        updatedUser.setAvatar(currentUser.getAvatar()); // ảnh sẽ cập nhật sau khi upload

                        String token = sharedPref.getString("accessToken", "");
                        UserService apiService = ApiClient.getClient().create(UserService.class);

                        apiService.updateUser(String.valueOf(currentUser.getId()), updatedUser, "Bearer " + token)
                                .enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            currentUser = response.body();
                                            sharedPref.edit().putString("user", gson.toJson(currentUser)).apply();

                                            tvFullName.setText(currentUser.getFullName());
                                            tvPhone.setText(currentUser.getPhone());
                                            tvEmail.setText(currentUser.getEmail());
                                            Toast.makeText(User_Profile.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(User_Profile.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                        Toast.makeText(User_Profile.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Huỷ", null)
                    .create();

            dialog.show();
        });

        // 2. Bắt sự kiện nút Save
        btnSave.setOnClickListener(v -> {
            String newName = etFullName.getText().toString();
            String newPhone = etPhone.getText().toString();
            String newEmail = etEmail.getText().toString();

            // Tạo object mới để gửi lên API
            User updatedUser = new User();
            updatedUser.setFullName(newName);
            updatedUser.setPhone(newPhone);
            updatedUser.setEmail(newEmail);
            updatedUser.setId(currentUser.getId());

            // Lấy token từ SharedPreferences
            String accessToken = sharedPref.getString("accessToken", "");
            int userId = currentUser.getId();

            UserService apiService = ApiClient.getClient().create(UserService.class);
            Call<User> call = apiService.updateUser(Integer.toString(userId), updatedUser, "Bearer " + accessToken);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Cập nhật user local
                        currentUser = response.body();

                        // Lưu vào SharedPreferences
                        String updatedJson = gson.toJson(currentUser);
                        sharedPref.edit().putString("user", updatedJson).apply();

                        // Update UI
                        tvFullName.setText(currentUser.getFullName());
                        tvPhone.setText(currentUser.getPhone());
                        tvEmail.setText(currentUser.getEmail());

                        Glide.with(User_Profile.this)
                                .load(currentUser.getAvatar())
                                .placeholder(R.drawable.ic_avatar)
                                .into(dialogAvatar);

                        Toast.makeText(User_Profile.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(User_Profile.this, "Cập nhật thất bại!" + response.code() + currentUser.getId(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(User_Profile.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadUserFromPrefs() {
        String userJson = sharedPref.getString("user", null);
        if (userJson != null) {
            currentUser = gson.fromJson(userJson, User.class);
            tvFullName.setText(currentUser.getFullName());
            tvUsername.setText(currentUser.getUsername());
            tvPhone.setText(currentUser.getPhone());
            tvEmail.setText(currentUser.getEmail());

            if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                ImageView avatarImageView = findViewById(R.id.avatarImageView);
                Glide.with(this)
                        .load(currentUser.getAvatar())
                        .placeholder(R.drawable.ic_avatar)
                        .into(avatarImageView);
            }
        } else {
            Log.e("User_Profile", "Không có dữ liệu user trong SharedPreferences");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Hiển thị ảnh mới (nếu muốn dùng Glide)
                // Glide.with(this).load(imageUri).into(dialogAvatar); // nếu dialogAvatar global
                uploadImageToServer(imageUri);
            }
        }
    }

    private void uploadImageToServer(Uri imageUri) {
        String filePath = FileUtils.getPath(this, imageUri); // cần hàm getPath()
        if (filePath == null) {
            Toast.makeText(this, "Không thể đọc ảnh từ thiết bị", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

        String token = sharedPref.getString("accessToken", "");
        UserService userService = ApiClient.getClient().create(UserService.class);

        userService.uploadAvatar(body, "Bearer " + token).enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getUrl();
                    currentUser.setAvatar(imageUrl); // cập nhật avatar cho currentUser
                    Toast.makeText(User_Profile.this, "Upload ảnh thành công!", Toast.LENGTH_SHORT).show();
                    // Load ảnh mới vào avatar ngoài màn chính

                    if (dialogAvatar != null) {
                        Glide.with(User_Profile.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_avatar)
                                .into(dialogAvatar);
                    }
                } else {
                    Toast.makeText(User_Profile.this, "Lỗi upload: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                Toast.makeText(User_Profile.this, "Lỗi mạng khi upload: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}