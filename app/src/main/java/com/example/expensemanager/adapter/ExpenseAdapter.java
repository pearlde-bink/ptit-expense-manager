package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.R;
import com.example.expensemanager.model.Category;
import com.example.expensemanager.model.Expense;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private List<Category> categoryList; // Danh sách category để ánh xạ categoryId

    public ExpenseAdapter(List<Expense> expenseList, List<Category> categoryList) {
        this.expenseList = expenseList;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.title.setText(expense.getTitle());
        holder.amount.setText(String.valueOf(expense.getAmount()));

        // Tìm Category tương ứng với categoryId
        String categoryId = String.valueOf(expense.getCategoryId());
        Category category = categoryList.stream()
                .filter(c -> c.getId() != null && c.getId().toString().equals(categoryId))
                .findFirst()
                .orElse(null);
        if (category != null) {
            holder.category.setText(category.getTitle()); // Hiển thị tên category
        } else {
            holder.category.setText("Unknown Category"); // Xử lý trường hợp không tìm thấy
        }
    }

    @Override
    public int getItemCount() {
        return expenseList != null ? expenseList.size() : 0;
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, category;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.income_title);
            amount = itemView.findViewById(R.id.income_amount);
            category = itemView.findViewById(R.id.income_category);
        }
    }
}