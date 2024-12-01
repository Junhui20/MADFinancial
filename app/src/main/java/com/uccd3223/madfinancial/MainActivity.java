package com.uccd3223.madfinancial;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private TextView balanceText;
    private TextView incomeText;
    private TextView expenseText;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        setupRecyclerView();
        setupAddButton();
        loadTransactions();
    }

    private void initializeViews() {
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        balanceText = findViewById(R.id.balanceText);
        incomeText = findViewById(R.id.incomeText);
        expenseText = findViewById(R.id.expenseText);
    }

    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter(this);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecyclerView.setAdapter(transactionAdapter);
    }

    private void setupAddButton() {
        FloatingActionButton addButton = findViewById(R.id.addTransactionButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }

    private void loadTransactions() {
        db.collection("transactions")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    List<Transaction> transactions = new ArrayList<>();
                    double totalIncome = 0;
                    double totalExpense = 0;

                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            Transaction transaction = document.toObject(Transaction.class);
                            transaction.setId(document.getId());
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
                });
    }

    private void updateBalanceTexts(double income, double expense) {
        double balance = income - expense;
        Locale locale = Locale.getDefault();
        balanceText.setText(String.format(locale, getString(R.string.amount_format), balance));
        incomeText.setText(String.format(locale, getString(R.string.amount_format), income));
        expenseText.setText(String.format(locale, getString(R.string.amount_format), Math.abs(expense)));
    }
}