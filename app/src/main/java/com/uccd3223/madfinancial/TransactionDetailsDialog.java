package com.uccd3223.madfinancial;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionDetailsDialog extends Dialog {
    private final Transaction transaction;
    private final Context context;

    public TransactionDetailsDialog(@NonNull Context context, Transaction transaction) {
        super(context);
        this.context = context;
        this.transaction = transaction;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_transaction_details);
        
        setupDialog();
    }

    private void setupDialog() {
        TextView amountText = findViewById(R.id.amountText);
        TextView typeText = findViewById(R.id.typeText);
        TextView categoryText = findViewById(R.id.categoryText);
        TextView dateText = findViewById(R.id.dateText);
        TextView timeText = findViewById(R.id.timeText);
        TextView noteText = findViewById(R.id.noteText);
        ImageView attachmentImage = findViewById(R.id.attachmentImage);
        Button editButton = findViewById(R.id.editButton);
        Button closeButton = findViewById(R.id.closeButton);

        // Format amount with RM symbol
        String amountStr = String.format(Locale.getDefault(), "RM %.2f", transaction.getAmount());
        amountText.setText(amountStr);
        
        // Set type with proper capitalization
        typeText.setText(transaction.getType().substring(0, 1).toUpperCase() + 
                        transaction.getType().substring(1));
        
        categoryText.setText(transaction.getCategory());

        // Format date and time
        Date date = new Date(transaction.getTimestamp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateText.setText(dateFormat.format(date));
        timeText.setText(timeFormat.format(date));

        noteText.setText(transaction.getDescription());

        // Handle attachment image
        if (transaction.getImageUri() != null && !transaction.getImageUri().isEmpty()) {
            attachmentImage.setVisibility(View.VISIBLE);
            attachmentImage.setImageURI(Uri.parse(transaction.getImageUri()));
        } else {
            attachmentImage.setVisibility(View.GONE);
        }

        // Handle edit button click
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTransactionActivity.class);
            intent.putExtra("TRANSACTION_ID", transaction.getId());
            intent.putExtra("EDIT_MODE", true);
            context.startActivity(intent);
            dismiss();
        });

        // Handle close button click
        closeButton.setOnClickListener(v -> dismiss());
    }
}
