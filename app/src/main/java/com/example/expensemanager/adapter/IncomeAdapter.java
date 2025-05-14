package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.R;
import com.example.expensemanager.model.Category;
import com.example.expensemanager.model.Income;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<Income> incomeList;
    private List<Category> categoryList; // Thêm danh sách category

    public IncomeAdapter(List<Income> incomeList, List<Category> categoryList) {
        this.incomeList = incomeList;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = incomeList.get(position);
        holder.title.setText(income.getTitle());
        holder.amount.setText(String.valueOf(income.getAmount()));

        // Tìm Category tương ứng với categoryId
        String categoryId = String.valueOf(income.getCategoryId());
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
        return incomeList != null ? incomeList.size() : 0;
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, category;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.income_title);
            amount = itemView.findViewById(R.id.income_amount);
            category = itemView.findViewById(R.id.income_category); // ID của TextView category
        }
    }
}