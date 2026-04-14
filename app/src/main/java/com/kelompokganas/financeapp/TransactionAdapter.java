package com.kelompokganas.financeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private final List<Transaction> transactions;
    private DecimalFormat df = new DecimalFormat("#,###");
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactions, OnItemClickListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        
        holder.txtTitle.setText(transaction.getTitle());
        String subtitle = transaction.getCategory() + " • " + transaction.getDate();
        holder.txtSubtitle.setText(subtitle);
        
        String formattedAmount = df.format(transaction.getAmount()).replace(',', '.');
        String amountText = (transaction.getType().equals("Pemasukan") ? "+ " : "- ") + "Rp " + formattedAmount;
        holder.txtAmount.setText(amountText);
        
        if (transaction.getType().equals("Pemasukan")) {
            holder.txtAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.income_green));
            holder.txtTag.setText("Pemasukan");
            holder.txtTag.setBackgroundResource(R.drawable.bg_tag_income);
            holder.imgTypeIcon.setImageResource(R.drawable.ic_plus);
            holder.imgTypeIcon.setBackgroundResource(R.drawable.bg_circle_income);
        } else {
            holder.txtAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.expense_red));
            holder.txtTag.setText("Pengeluaran");
            holder.txtTag.setBackgroundResource(R.drawable.bg_tag_expense);
            holder.imgTypeIcon.setImageResource(R.drawable.ic_minus);
            holder.imgTypeIcon.setBackgroundResource(R.drawable.bg_circle_expense);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtSubtitle, txtAmount, txtTag;
        ImageView imgTypeIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSubtitle = itemView.findViewById(R.id.txtSubtitle);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtTag = itemView.findViewById(R.id.txtTag);
            imgTypeIcon = itemView.findViewById(R.id.imgTypeIcon);
        }
    }
}