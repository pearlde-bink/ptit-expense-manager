package com.example.expensemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private Context context;
    private List<Reminder> reminders;

    public ReminderAdapter(Context context, List<Reminder> reminders) {
        this.context = context;
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.title.setText(reminder.getTitle());
        holder.amount.setText("$" + String.format(Locale.getDefault(), "%.2f", reminder.getAmount()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.dueDate.setText("Due on: " + dateFormat.format(reminder.getDueDate()));
        holder.state.setText(reminder.getState() ? "Active" : "Inactive");
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, dueDate, state;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reminder_title);
            amount = itemView.findViewById(R.id.reminder_amount);
            dueDate = itemView.findViewById(R.id.reminder_due_date);
            state = itemView.findViewById(R.id.reminder_state);
        }
    }
}