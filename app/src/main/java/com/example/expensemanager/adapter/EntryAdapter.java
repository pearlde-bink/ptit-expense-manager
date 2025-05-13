package com.example.expensemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.model.Entry;
import com.example.expensemanager.model.Entry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    private Context context;
    private List<Entry> entries;

    public EntryAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entries = entries;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Entry entry = entries.get(position);

        // Set title
        holder.entryTitle.setText(entry.getTitle());

        // Set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.entryDate.setText(dateFormat.format(entry.getDate()));

        // Set amount and VAT (assuming the server sends a field for amount)
        String amountSign = entry.getEntryType().equals("expense") ? "- $" : "+ $";
        String amountText = amountSign + String.format(Locale.getDefault(), "%.0f", entry.getAmount());
        holder.entryAmount.setText(amountText);

        // Set payment method (we assume this data is also sent in the response)
        holder.paymentMethod.setText("cash");

        // Set category icon based on the category name
        switch (entry.getCategory().toLowerCase()) {
            case "food":
                holder.categoryIcon.setImageResource(R.drawable.food);
                break;
            case "uber":
                holder.categoryIcon.setImageResource(R.drawable.vehicle);
                break;
            case "shopping":
                holder.categoryIcon.setImageResource(R.drawable.shopping);
                break;
            case "rent":
                holder.categoryIcon.setImageResource(R.drawable.piggy_bank);
                break;
            case "bill":
                holder.categoryIcon.setImageResource(R.drawable.bill);
                break;
            case "movie":
                holder.categoryIcon.setImageResource(R.drawable.entertainment);
                break;
            default:
                holder.categoryIcon.setImageResource(R.drawable.ic_star);
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView entryTitle;
        TextView entryDate;
        TextView entryAmount;
        TextView paymentMethod;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            entryTitle = itemView.findViewById(R.id.entry_title);
            entryDate = itemView.findViewById(R.id.entry_date);
            entryAmount = itemView.findViewById(R.id.entry_amount);
            paymentMethod = itemView.findViewById(R.id.payment_method);
        }
    }
}