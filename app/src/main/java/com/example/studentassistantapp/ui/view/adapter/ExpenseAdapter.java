package com.example.studentassistantapp.ui.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentassistantapp.R;
import com.example.studentassistantapp.data.model.ExpenseModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseAdapter extends ListAdapter<ExpenseModel, ExpenseAdapter.ExpenseViewHolder> {

    public ExpenseAdapter() {
        super(new DiffUtil.ItemCallback<ExpenseModel>() {
            @Override
            public boolean areItemsTheSame(@NonNull ExpenseModel oldItem, @NonNull ExpenseModel newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ExpenseModel oldItem, @NonNull ExpenseModel newItem) {
                return oldItem.getAmount() == newItem.getAmount() &&
                        oldItem.getCategory().equals(newItem.getCategory()) &&
                        oldItem.getDescription().equals(newItem.getDescription()) &&
                        oldItem.getTimestamp() == newItem.getTimestamp();
            }
        });
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseModel expense = getItem(position);
        holder.bind(expense);
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategory;
        private final TextView tvAmount;
        private final TextView tvDescription;
        private final TextView tvSelectDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSelectDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(ExpenseModel expense) {
            tvCategory.setText(expense.getCategory());
            tvAmount.setText(String.format(Locale.getDefault(), "৳%.2f", expense.getAmount()));
            tvDescription.setText(expense.getDescription());
            tvSelectDate.setText(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    .format(new Date(expense.getTimestamp())));
        }
    }
}