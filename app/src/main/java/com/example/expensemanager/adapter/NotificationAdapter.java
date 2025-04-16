package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.expensemanager.R;
import com.example.expensemanager.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.icon.setImageResource(notification.getIconResId());
        holder.title.setText(notification.getTitle());
        holder.description.setText(notification.getDescription());
        holder.timestamp.setText(notification.getTimestamp());

        // Handle more button click (e.g., show a menu)
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your logic here (e.g., show a popup menu)
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView description;
        TextView timestamp;
        ImageView moreButton;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.notification_icon);
            title = itemView.findViewById(R.id.notification_title);
            description = itemView.findViewById(R.id.notification_description);
            timestamp = itemView.findViewById(R.id.notification_timestamp);
            moreButton = itemView.findViewById(R.id.more_button);
        }
    }
}
