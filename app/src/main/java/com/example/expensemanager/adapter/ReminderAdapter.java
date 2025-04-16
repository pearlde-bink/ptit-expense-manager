package com.example.expensemanager.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.SetReminder;
import com.example.expensemanager.model.Reminder;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminders;
    private Activity activity; // To call startActivityForResult
    public ReminderAdapter(Activity activity, List<Reminder> reminders){
        this.reminders = reminders;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.title.setText(reminder.getTitle());
        holder.amount.setText("$" + reminder.getAmount());
        holder.startDate.setText("Start: " + reminder.getStartDate());
        holder.endDate.setText("End: " + reminder.getEndDate());
        holder.state.setText(reminder.isState() ? "Over" : "Due on");

        // Handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SetReminder.class);
                intent.putExtra("reminder", reminder);
                intent.putExtra("position", position);
                activity.startActivityForResult(intent, 1); // Request code 1
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public void updateReminder(int position, Reminder updatedReminder) {
        reminders.set(position, updatedReminder);
        notifyItemChanged(position);
    }
    static class ReminderViewHolder extends RecyclerView.ViewHolder{
        TextView startDate, title, state, amount, endDate;
        ImageView moreButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            startDate = itemView.findViewById(R.id.reminder_startDate);
            title = itemView.findViewById(R.id.reminder_title);
            state = itemView.findViewById(R.id.reminder_state);
            amount = itemView.findViewById(R.id.reminder_amount);
            endDate = itemView.findViewById(R.id.reminder_endDate);
            moreButton = itemView.findViewById(R.id.more_button);
        }
    }
}


//    @Override
//    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
//        Reminder reminder = reminders.get(position);
//        holder.title.setText(reminder.getTitle());
//        holder.amount.setText("$" + reminder.getAmount().toString());
//        holder.startDate.setText("Reminder Date: " + reminder.getStartDate());
//        holder.endDate.setText("End: " + reminder.getEndDate());
//        holder.state.setText(reminder.isState() ? "Over" : "Due on");
//
//        holder.moreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), SetReminder.class);
//                intent.putExtra("reminder", reminder);
//                view.getContext().startActivity(intent);
//            }
//        });
//    }
