package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.R;
import com.example.expensemanager.model.Budget;
import com.example.expensemanager.model.Category;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<Budget> budgets;
    private List<Category> categories;

    public BudgetAdapter(List<Budget> budgets, List<Category> categories) {
        this.budgets = budgets;
        this.categories = categories;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
        notifyDataSetChanged();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
        holder.amount.setText("$" + String.format("%.2f", budget.getAmount()));
        holder.period.setText(budget.getMonth() + "/" + budget.getYear());

        // Map categoryId to category name
//        String categoryName = "Unknown";
//        for (Category category : categories) {
//            if (category.getId() == budget.getCategoryId()) {
//                categoryName = category.getTitle();
//                break;
//            }
//        }
//        holder.category.setText(categoryName);
    }

    @Override
    public int getItemCount() {
        return budgets != null ? budgets.size() : 0;
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
//        TextView category;
        TextView amount;
        TextView period;

        BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
//            category = itemView.findViewById(R.id.budget_category);
            amount = itemView.findViewById(R.id.budget_amount);
            period = itemView.findViewById(R.id.budget_period);
        }
    }
}