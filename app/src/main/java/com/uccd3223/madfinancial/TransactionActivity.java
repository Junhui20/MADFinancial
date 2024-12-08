package com.uccd3223.madfinancial;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionActivity extends BaseActivity {
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private TextView balanceText;
    private TextView incomeText;
    private TextView expenseText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        setupToolbar("Transactions");
        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupViews();
        loadTransactions();
    }

    private void initializeViews() {
        balanceText = findViewById(R.id.balanceText);
        incomeText = findViewById(R.id.incomeText);
        expenseText = findViewById(R.id.expenseText);
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);

        if (balanceText == null || incomeText == null || expenseText == null) {
            throw new IllegalStateException("Required views not found in layout");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions(); // Reload transactions when returning to this activity
    }

    private void setupViews() {
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(new ArrayList<>());
        transactionsRecyclerView.setAdapter(transactionAdapter);

        FloatingActionButton addButton = findViewById(R.id.addTransactionButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(TransactionActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });
    }

    private void loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final double[] totalIncome = {0};
        final double[] totalExpense = {0};

        String[] projection = {
                "id",
                "type",
                "amount",
                "name",
                "category",
                "category_name",
                "category_color",
                "timestamp",
                "description",
                "is_recurring",
                "recurring_period",
                "image_uri"
        };

        String sortOrder = "timestamp DESC";

        try (Cursor cursor = db.query(
                "transactions",
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        )) {
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                
                // Debug log
                Log.d("TransactionActivity", "Transaction: type=" + type + ", amount=" + amount);
                
                // Calculate totals (case-insensitive comparison)
                if ("income".equalsIgnoreCase(type)) {
                    totalIncome[0] += amount;
                    Log.d("TransactionActivity", "Added to income: " + amount + ", Total: " + totalIncome[0]);
                } else if ("expense".equalsIgnoreCase(type)) {
                    totalExpense[0] += Math.abs(amount);
                    Log.d("TransactionActivity", "Added to expense: " + amount + ", Total: " + totalExpense[0]);
                }

                Transaction transaction = new Transaction(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        type,
                        amount,
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category_color")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("is_recurring")) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow("recurring_period")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image_uri"))
                );
                transactions.add(transaction);
            }
        }

        // Log final totals
        Log.d("TransactionActivity", "Final totals - Income: " + totalIncome[0] + ", Expense: " + totalExpense[0]);

        // Update UI with totals
        final List<Transaction> finalTransactions = transactions;
        runOnUiThread(() -> {
            double totalBalance = totalIncome[0] - totalExpense[0];
            Log.d("TransactionActivity", "Setting UI - Balance: " + totalBalance);
            balanceText.setText(String.format(Locale.getDefault(), "RM %.2f", totalBalance));
            incomeText.setText(String.format(Locale.getDefault(), "RM %.2f", totalIncome[0]));
            expenseText.setText(String.format(Locale.getDefault(), "RM %.2f", totalExpense[0]));
            transactionAdapter.updateTransactions(finalTransactions);
        });
    }
}