package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.R;
import com.example.expensemanager.model.Goal;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<Goal> goals;

    public GoalAdapter(List<Goal> goals) {
        this.goals = goals;
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

        GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.goal_icon);
            title = itemView.findViewById(R.id.goal_title);
            currentAmount = itemView.findViewById(R.id.goal_current_amount);
            targetAmount = itemView.findViewById(R.id.goal_target_amount);
            progress = itemView.findViewById(R.id.goal_progress);
            deadline = itemView.findViewById(R.id.goal_deadline); // Add this ID to item_goal.xml
        }
    }
}