package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;

import java.text.DateFormatSymbols;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<BudgetDisplay> budgets;

    public BudgetAdapter(List<BudgetDisplay> budgets) {
        this.budgets = budgets;
    }

    public void setBudgets(List<BudgetDisplay> budgets) {
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
        BudgetDisplay budget = budgets.get(position);
        holder.budgetText.setText(String.format("$%.2f / $%.2f", budget.expense, budget.budget));
        String monthName = new DateFormatSymbols().getMonths()[budget.month - 1];
        holder.period.setText(String.format("%s %d", monthName, budget.year));
    }

    @Override
    public int getItemCount() {
        return budgets != null ? budgets.size() : 0;
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetText;
        TextView period;

        BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetText = itemView.findViewById(R.id.budget_text);
            period = itemView.findViewById(R.id.budget_period);
        }
    }

    public static class BudgetDisplay {
        int month;
        int year;
        double expense;
        double budget;

        public BudgetDisplay(int month, int year, double expense, double budget) {
            this.month = month;
            this.year = year;
            this.expense = expense;
            this.budget = budget;
        }
    }
}