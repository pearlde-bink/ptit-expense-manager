package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.R;
import com.example.expensemanager.model.Budget;
import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<Budget> budgets;

    public BudgetAdapter(List<Budget> budgets) {
        this.budgets = budgets;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgets.get(position);
        // Format month and year (e.g., "May 2025")
        String monthName = new DateFormatSymbols(Locale.getDefault()).getMonths()[budget.getMonth() - 1];
        holder.budgetPeriod.setText(String.format("%s %d", monthName, budget.getYear()));
        // Format amount for previous months (e.g., "$1000.00")
        holder.budgetText.setText(String.format("$%.2f", budget.getAmount()));
    }

    @Override
    public int getItemCount() {
        return budgets != null ? budgets.size() : 0;
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetPeriod;
        TextView budgetText;

        BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetPeriod = itemView.findViewById(R.id.budget_period);
            budgetText = itemView.findViewById(R.id.budget_text);
        }
    }
}