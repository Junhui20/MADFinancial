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
    private final List<Transaction> transactions;
    private final SimpleDateFormat dateFormat;

    public TransactionAdapter(Context context) {
        this.transactions = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateTransactions(List<Transaction> newTransactions) {
        int oldSize = transactions.size();
        transactions.clear();
        notifyItemRangeRemoved(0, oldSize);
        
        transactions.addAll(newTransactions);
        notifyItemRangeInserted(0, newTransactions.size());
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView amountText;
        private final TextView categoryText;
        private final TextView descriptionText;
        private final TextView dateText;

        TransactionViewHolder(View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.amountText);
            categoryText = itemView.findViewById(R.id.categoryText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dateText = itemView.findViewById(R.id.dateText);
        }

        void bind(Transaction transaction) {
            Context context = itemView.getContext();
            Locale locale = Locale.getDefault();
            String amountStr = String.format(locale, "%s %.2f", 
                transaction.getCurrency(), Math.abs(transaction.getAmount()));
            
            amountText.setText(amountStr);
            categoryText.setText(transaction.getCategory());
            descriptionText.setText(transaction.getDescription());
            dateText.setText(dateFormat.format(transaction.getDate()));

            int textColor = transaction.getAmount() >= 0 ? 
                ContextCompat.getColor(context, R.color.colorIncome) : 
                ContextCompat.getColor(context, R.color.colorExpense);
            amountText.setTextColor(textColor);
        }
    }
}
