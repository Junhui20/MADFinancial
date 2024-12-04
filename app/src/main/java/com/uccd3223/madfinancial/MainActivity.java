package com.uccd3223.madfinancial;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private TextView balanceText;
    private TextView incomeText;
    private TextView expenseText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });
    }

    private void loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double totalIncome = 0;
        double totalExpense = 0;

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
                Transaction transaction = new Transaction(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
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

                if (transaction.getType().equals("income")) {
                    totalIncome += transaction.getAmount();
                } else {
                    totalExpense += Math.abs(transaction.getAmount());
                }
            }
        }

        transactionAdapter.updateTransactions(transactions);
        updateBalanceTexts(totalIncome, totalExpense);
    }

    private void updateBalanceTexts(double income, double expense) {
        double balance = income - expense;
        String balanceString = String.format(Locale.getDefault(), "%.2f", balance);
        String incomeString = String.format(Locale.getDefault(), "%.2f", income);
        String expenseString = String.format(Locale.getDefault(), "%.2f", expense);

        balanceText.setText("Balance: RM " + balanceString);
        incomeText.setText("Income: RM " + incomeString);
        expenseText.setText("Expense: RM " + expenseString);
    }
}