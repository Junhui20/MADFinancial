package com.uccd3223.madfinancial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions;
    private final SimpleDateFormat dateFormat;
    private final Context context;

    public TransactionAdapter(Context context) {
        this.context = context;
        this.transactions = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.bind(transactions.get(position));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        final TextView amountText;
        final TextView categoryText;
        final TextView dateText;
        final TextView descriptionText;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.amountText);
            categoryText = itemView.findViewById(R.id.categoryText);
            dateText = itemView.findViewById(R.id.dateText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
        }

        void bind(Transaction transaction) {
            String amountStr = String.format("%s %.2f", 
                transaction.getCurrency(), 
                Math.abs(transaction.getAmount()));
            
            amountText.setText(amountStr);
            categoryText.setText(transaction.getCategory());
            descriptionText.setText(transaction.getDescription());
            dateText.setText(dateFormat.format(transaction.getDate()));

            int color = transaction.getType().equals("income") ? 
                ContextCompat.getColor(context, R.color.colorIncome) : 
                ContextCompat.getColor(context, R.color.colorExpense);
            amountText.setTextColor(color);
        }
    }
}
