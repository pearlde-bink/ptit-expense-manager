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
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<Goal> goals;

    public GoalAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.title.setText(goal.getTitle());
        holder.currentAmount.setText("$" + goal.getCurrentAmount());
        holder.targetAmount.setText("$" + goal.getTargetAmount());
        holder.progress.setProgress(goal.getProgress());
        // Icon is set in the layout (ic_bike); can be made dynamic if needed
    }

    @Override
    public int getItemCount() {
        int count = goals.size();
        android.util.Log.d("GoalAdapter", "Item count: " + count);
        return count;
    }

    public void addGoal(Goal newGoal) {
        goals.add(newGoal);
        notifyItemInserted(goals.size() - 1);
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView currentAmount;
        TextView targetAmount;
        LinearProgressIndicator progress;

        GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.goal_icon);
            title = itemView.findViewById(R.id.goal_title);
            currentAmount = itemView.findViewById(R.id.goal_current_amount);
            targetAmount = itemView.findViewById(R.id.goal_target_amount);
            progress = itemView.findViewById(R.id.goal_progress);
        }
    }
}