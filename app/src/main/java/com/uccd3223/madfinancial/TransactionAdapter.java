package com.uccd3223.madfinancial;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        
        holder.nameText.setText(transaction.getName());
        holder.categoryText.setText(transaction.getCategoryName());
        holder.dateText.setText(dateFormat.format(new Date(transaction.getTimestamp())));
        
        // Format amount with proper sign and color
        String amountText = String.format(Locale.getDefault(), "RM %.2f", Math.abs(transaction.getAmount()));
        if (transaction.getType().equals("income")) {
            holder.amountText.setTextColor(Color.parseColor("#4CAF50")); // Green
            amountText = "+ " + amountText;
        } else {
            holder.amountText.setTextColor(Color.parseColor("#F44336")); // Red
            amountText = "- " + amountText;
        }
        holder.amountText.setText(amountText);

        // Set category color indicator
        try {
            holder.categoryIndicator.setBackgroundColor(Color.parseColor(transaction.getCategoryColor()));
        } catch (Exception e) {
            holder.categoryIndicator.setBackgroundColor(Color.GRAY);
        }

        // Handle recurring transaction indicator
        holder.recurringIcon.setVisibility(transaction.isRecurring() ? View.VISIBLE : View.GONE);

        // Handle attachment indicator
        if (transaction.getImageUri() != null && !transaction.getImageUri().isEmpty()) {
            holder.attachmentIcon.setVisibility(View.VISIBLE);
        } else {
            holder.attachmentIcon.setVisibility(View.GONE);
        }

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            TransactionDetailsDialog dialog = new TransactionDetailsDialog(holder.itemView.getContext(), transaction);
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactions = new ArrayList<>(newTransactions);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View categoryIndicator;
        TextView nameText;
        TextView categoryText;
        TextView amountText;
        TextView dateText;
        ImageView recurringIcon;
        ImageView attachmentIcon;

        ViewHolder(View itemView) {
            super(itemView);
            categoryIndicator = itemView.findViewById(R.id.categoryIndicator);
            nameText = itemView.findViewById(R.id.nameText);
            categoryText = itemView.findViewById(R.id.categoryText);
            amountText = itemView.findViewById(R.id.amountText);
            dateText = itemView.findViewById(R.id.dateText);
            recurringIcon = itemView.findViewById(R.id.recurringIcon);
            attachmentIcon = itemView.findViewById(R.id.attachmentIcon);
        }
    }
}
