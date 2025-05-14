package com.example.expensemanager.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.BudgetActivity;
import com.example.expensemanager.R;
import com.example.expensemanager.api.ApiClient;
import com.example.expensemanager.api.BudgetService;
import com.example.expensemanager.model.Budget;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<Budget> budgets;
    private List<BudgetActivity.BudgetWithExpense> budgetsWithExpenses;
    private Context context;
    private BudgetService budgetService;

    public BudgetAdapter(List<Budget> budgets, Context context) {
        this.budgets = budgets;
        this.budgetsWithExpenses = new ArrayList<>();
        this.context = context;
        this.budgetService = ApiClient.getClient().create(BudgetService.class);
    }

    public BudgetAdapter(List<Budget> budgets) {
        this.budgets = budgets;
        this.budgetsWithExpenses = new ArrayList<>();
        this.budgetService = ApiClient.getClient().create(BudgetService.class);
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
        this.budgetsWithExpenses.clear();
        notifyDataSetChanged();
    }

    public void setBudgetsWithExpenses(List<BudgetActivity.BudgetWithExpense> budgetsWithExpenses) {
        this.budgetsWithExpenses = budgetsWithExpenses;
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
        Budget budget;
        if (!budgetsWithExpenses.isEmpty()) {
            BudgetActivity.BudgetWithExpense budgetWithExpense = budgetsWithExpenses.get(position);
            budget = budgetWithExpense.getBudget();
            double totalExpense = budgetWithExpense.getTotalExpense();
            double budgetAmount = budget.getAmount();
            holder.budgetText.setText(String.format("$%.2f / $%.2f", totalExpense, budgetAmount));
        } else {
            budget = budgets.get(position);
            holder.budgetText.setText(String.format("$0.00 / $%.2f", budget.getAmount()));
        }

        String monthName = new DateFormatSymbols(Locale.getDefault()).getMonths()[budget.getMonth() - 1];
        holder.budgetPeriod.setText(String.format("%s %d", monthName, budget.getYear()));

        // Setup menu button
        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.menuButton);
            popup.inflate(R.menu.budget_menu);

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_edit_budget) {
                    showEditBudgetDialog(budget, holder.getAdapterPosition());
                    return true;
                } else if (id == R.id.menu_delete_budget) {
                    showDeleteConfirmationDialog(budget);
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return budgetsWithExpenses.isEmpty() ? (budgets != null ? budgets.size() : 0) : budgetsWithExpenses.size();
    }

    private void showEditBudgetDialog(Budget budget, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_budget, null);
        builder.setView(dialogView);

        TextInputEditText amountInput = dialogView.findViewById(R.id.amount_input);
        MaterialAutoCompleteTextView monthInput = dialogView.findViewById(R.id.month_input);
        TextInputEditText yearInput = dialogView.findViewById(R.id.year_input);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnOk = dialogView.findViewById(R.id.btn_ok);

        // Set existing budget data
        amountInput.setText(String.valueOf(budget.getAmount()));
        String[] months = context.getResources().getStringArray(R.array.months);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, months);
        monthInput.setAdapter(monthAdapter);
        monthInput.setText(months[budget.getMonth() - 1], false);
        yearInput.setText(String.valueOf(budget.getYear()));

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString().trim();
            String monthStr = monthInput.getText().toString().trim();
            String yearStr = yearInput.getText().toString().trim();

            if (amountStr.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(context, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid amount format", Toast.LENGTH_SHORT).show();
                return;
            }

            int month = getMonthIndex(monthStr) + 1;
            int year;
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid year format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update budget object
            budget.setAmount(amount);
            budget.setMonth(month);
            budget.setYear(year);

            // Call API to update budget
            String accessToken = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    .getString("accessToken", "");
            
            budgetService.updateBudget("Bearer " + accessToken, budget.getId(), budget)
                    .enqueue(new Callback<Budget>() {
                        @Override
                        public void onResponse(Call<Budget> call, Response<Budget> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Budget updated successfully", Toast.LENGTH_SHORT).show();
                                notifyItemChanged(position);
                            } else {
                                Toast.makeText(context, "Failed to update budget: " + response.code(), 
                                    Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Budget> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteConfirmationDialog(Budget budget) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Budget")
                .setMessage("Are you sure you want to delete this budget?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String accessToken = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            .getString("accessToken", "");
                    
                    budgetService.deleteBudget("Bearer " + accessToken, budget.getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        int position = budgets.indexOf(budget);
                                        if (position != -1) {
                                            budgets.remove(position);
                                            notifyItemRemoved(position);
                                        }
                                        Toast.makeText(context, "Budget deleted successfully", 
                                            Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Failed to delete budget: " + response.code(), 
                                            Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(context, "Error: " + t.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getMonthIndex(String monthName) {
        String[] months = context.getResources().getStringArray(R.array.months);
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(monthName)) {
                return i;
            }
        }
        return -1;
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetPeriod;
        TextView budgetText;
        ImageButton menuButton;

        BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetPeriod = itemView.findViewById(R.id.budget_period);
            budgetText = itemView.findViewById(R.id.budget_text);
            menuButton = itemView.findViewById(R.id.btn_budget_menu);
        }
    }
}