package com.example.expensemanager.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.GoalService;
import com.example.expensemanager.model.AddToGoalRequest;
import com.example.expensemanager.model.Goal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<Goal> goals;
    private Context context;

    private GoalUpdateListener listener; // Đối tượng callback để gọi khi cập nhật

    public interface GoalUpdateListener {
        void onGoalUpdated();
    }

    public GoalAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    public GoalAdapter(Context context, List<Goal> goals, GoalUpdateListener listener) {
        this.context = context;
        this.goals = goals;
        this.listener = listener; // Nhận listener từ Activity
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.title.setText(goal.getTitle());
        holder.currentAmount.setText("$" + String.format("%.2f", goal.getCurrentAmount()));
        holder.targetAmount.setText("$" + String.format("%.2f", goal.getTargetAmount()));
        holder.progress.setProgress(goal.getProgress());

        // Format the deadline (from "yyyy-MM-dd" to "dd/MM/yyyy")
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDeadline = outputFormat.format(inputFormat.parse(goal.getDeadline()));
            holder.deadline.setText(formattedDeadline);
        } catch (ParseException e) {
            holder.deadline.setText("No deadline");
        }

        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.menuButton);
            popup.inflate(R.menu.goal_menu); // res/menu/goal_menu.xml

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.menu_add_money) {
                    showAddMoneyDialog(goal, v.getContext(), position);
                    return true;
                } else if (id == R.id.menu_edit_goal) {
                    showEditGoalDialog(goal, v.getContext(), holder.getAdapterPosition());
                    return true;
                } else if (id == R.id.menu_delete_goal) {
                    showDeleteGoalDialog(holder.itemView.getContext(), position);
                    return true;
                }

                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return goals != null ? goals.size() : 0;
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView currentAmount;
        TextView targetAmount;
        LinearProgressIndicator progress;
        TextView deadline;

        ImageButton menuButton;

        GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.goal_icon);
            title = itemView.findViewById(R.id.goal_title);
            currentAmount = itemView.findViewById(R.id.goal_current_amount);
            targetAmount = itemView.findViewById(R.id.goal_target_amount);
            progress = itemView.findViewById(R.id.goal_progress);
            deadline = itemView.findViewById(R.id.goal_deadline); // Add this ID to item_goal.xml
            menuButton = itemView.findViewById(R.id.btn_goal_menu);
        }
    }

    private void showAddMoneyDialog(Goal goal, Context context, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_money_goal, null);
        builder.setView(dialogView);

        EditText inputAmount = dialogView.findViewById(R.id.input_amount);
        TextView textPreview = dialogView.findViewById(R.id.text_total_preview);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnSubmit = dialogView.findViewById(R.id.btn_submit);

        double currentAmount = goal.getCurrentAmount();  // ví dụ 300
        double targetAmount = goal.getTargetAmount();    // ví dụ 600
        textPreview.setText("Total money after adding: $" + currentAmount + " / $" + targetAmount);

        AlertDialog dialog = builder.create();

        // Cập nhật preview khi người dùng nhập
        inputAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double added = 0;
                try {
                    added = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    // ignore
                }
                double total = currentAmount + added;
                textPreview.setText("Total money after adding: $" + total + " / $" + targetAmount);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String input = inputAmount.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            double addedMoney = Double.parseDouble(input);

            AddToGoalRequest request = new AddToGoalRequest(goal.getId(), addedMoney);
            String token = "Bearer " + getAccessToken(context);

            GoalService goalService = ApiClient.getClient().create(GoalService.class);
            goalService.createGoal(request, token).enqueue(new Callback<Goal>() {
                @Override
                public void onResponse(Call<Goal> call, Response<Goal> response) {
                    if (response.isSuccessful()) {
                        Goal updatedGoal = response.body();
                        if (updatedGoal != null) {
                            // Cập nhật item trong danh sách
                            goals.set(position, updatedGoal);

                            // Thông báo adapter cập nhật item
                            notifyItemChanged(position);
                            if (listener != null) {
                                listener.onGoalUpdated();
                            }

                            Toast.makeText(context, "Added $" + addedMoney + " to goal " + updatedGoal.getTitle(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Response empty", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Failed to add money " + response.code() , Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Goal> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showEditGoalDialog(Goal goal, Context context, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_goal, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextInputEditText titleInput = dialogView.findViewById(R.id.goal_title_input);
        TextInputEditText amountInput = dialogView.findViewById(R.id.amount_input);
        MaterialAutoCompleteTextView contributionTypeInput = dialogView.findViewById(R.id.contribution_type_input);
        TextInputEditText deadlineInput = dialogView.findViewById(R.id.deadline_input);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnOk = dialogView.findViewById(R.id.btn_ok);

        // Set existing goal data
        titleInput.setText(goal.getTitle());
        amountInput.setText(String.valueOf(goal.getTargetAmount()));
        contributionTypeInput.setText(goal.getContributionType()); // Assuming you have this in your Goal model
        deadlineInput.setText(goal.getDeadline()); // Assuming it's a String like "2025-05-14"

        // Setup contribution type dropdown
        String[] contributionTypes = {"Onetime", "Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, contributionTypes);
        contributionTypeInput.setAdapter(adapter);

        // Handle deadline picker
        deadlineInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                deadlineInput.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // OK button
        btnOk.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String amountStr = amountInput.getText().toString().trim();
            String contributionType = contributionTypeInput.getText().toString().trim();
            String displayedDate = deadlineInput.getText().toString(); // ex: 14/05/2025


            if (title.isEmpty() || amountStr.isEmpty() || contributionType.isEmpty() || displayedDate.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double targetAmount;
            try {
                targetAmount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Amount must be a valid number", Toast.LENGTH_SHORT).show();
                return;
            }

            goal.setTitle(title);
            goal.setTargetAmount(targetAmount);
            goal.setContributionType(contributionType);
            goal.setDeadline(displayedDate); // Định dạng yyyy-MM-dd nếu backend yêu cầu

            String token = "Bearer " + getAccessToken(context);
            GoalService service = ApiClient.getClient().create(GoalService.class);

            service.updateGoal(goal.getId(), goal, token).enqueue(new Callback<Goal>() {
                @Override
                public void onResponse(Call<Goal> call, Response<Goal> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Goal updatedGoal = response.body();

                        // Cập nhật lại goal trong list
                        goals.set(position, updatedGoal); // position là index trong adapter của item goal

                        // Refresh UI
                        notifyItemChanged(position);

                        Toast.makeText(context, "Goal updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Goal> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Notify adapter
            notifyItemChanged(position);

            Toast.makeText(context, "Goal updated successfully", Toast.LENGTH_SHORT).show();

            // TODO: Call update goal API here using goal.getId(), title, targetAmount, contributionType, deadline

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteGoalDialog(Context context, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete this goal?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String token = "Bearer " + getAccessToken(context);
                    GoalService service = ApiClient.getClient().create(GoalService.class);
                    Goal goal = goals.get(position); // Lấy goal theo vị trí

                    service.deleteGoal(goal.getId(), token).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                goals.remove(position);
                                notifyItemRemoved(position); // Animation mượt mà khi item bị xoá
                                notifyItemRangeChanged(position, goals.size()); // Cập nhật vị trí các item còn lại
                                Toast.makeText(context, "Goal deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to delete goal", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String getAccessToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPref.getString("accessToken", null);
    }
}