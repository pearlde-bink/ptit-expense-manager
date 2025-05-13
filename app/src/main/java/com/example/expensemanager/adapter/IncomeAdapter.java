package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.R;
import com.example.expensemanager.model.Income;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
    private List<Income> incomes;

    public IncomeAdapter(List<Income> incomes) {
        this.incomes = incomes;
    }

    public void setIncomes(List<Income> incomes) {
        this.incomes = incomes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = incomes.get(position);

        // Set title
        holder.title.setText(income.getTitle());

        // Set amount
        holder.amount.setText("$" + String.format(Locale.getDefault(), "%.2f", income.getAmount()));

        // Set category
        holder.category.setText(income.getCategory());

        // Set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.date.setText(dateFormat.format(income.getDate()));

        // Set category icon based on category
        switch (income.getCategory().toLowerCase()) {
            case "salary":
                holder.icon.setImageResource(R.drawable.salary);
                break;
            case "rewards":
                holder.icon.setImageResource(R.drawable.rewards);
                break;
            default:
                holder.icon.setImageResource(R.drawable.ic_star);
        }
    }

    @Override
    public int getItemCount() {
        return incomes != null ? incomes.size() : 0;
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView amount;
        TextView category;
        TextView date;

        IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.income_icon);
            title = itemView.findViewById(R.id.income_title);
            amount = itemView.findViewById(R.id.income_amount);
            category = itemView.findViewById(R.id.income_category);
            date = itemView.findViewById(R.id.income_date);
        }
    }
}