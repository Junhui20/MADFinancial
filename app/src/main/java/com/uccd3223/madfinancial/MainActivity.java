package com.uccd3223.madfinancial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private TextView balanceText, incomeText, expenseText;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        transactionRecyclerView = findViewById(R.id.transactionRecyclerView);
        balanceText = findViewById(R.id.balanceText);
        incomeText = findViewById(R.id.incomeText);
        expenseText = findViewById(R.id.expenseText);
        FloatingActionButton addButton = findViewById(R.id.addTransactionButton);

        // Setup RecyclerView
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(this);
        transactionRecyclerView.setAdapter(transactionAdapter);

        // Setup FAB
        addButton.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, AddTransactionActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }

    private void loadTransactions() {
        db.collection("transactions")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Transaction> transactions = new ArrayList<>();
                    double totalIncome = 0;
                    double totalExpense = 0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Transaction transaction = document.toObject(Transaction.class);
                        transaction.setId(document.getId());
                        transactions.add(transaction);

                        if (transaction.getType().equals("income")) {
                            totalIncome += transaction.getAmount();
                        } else {
                            totalExpense += Math.abs(transaction.getAmount());
                        }
                    }

                    transactionAdapter.updateTransactions(transactions);
                    updateBalanceTexts(totalIncome, totalExpense);
                })
                .addOnFailureListener(e -> {
                    String errorMessage = "Error loading transactions";
                    if (e.getMessage() != null && e.getMessage().contains("PERMISSION_DENIED")) {
                        errorMessage = "Permission denied. Please check Firebase rules.";
                    }
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    private void updateBalanceTexts(double income, double expense) {
        String defaultCurrency = "MYR"; // You can make this configurable later
        balanceText.setText(String.format("%s %.2f", defaultCurrency, income - expense));
        incomeText.setText(String.format("%s %.2f", defaultCurrency, income));
        expenseText.setText(String.format("%s %.2f", defaultCurrency, expense));
    }
}