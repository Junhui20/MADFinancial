package com.uccd3223.madfinancial;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity implements CategoryAdapter.OnCategorySelectedListener {
    private MaterialButtonToggleGroup typeToggleGroup;
    private TextInputEditText amountInput;
    private AutoCompleteTextView currencyDropdown;
    private TextInputEditText descriptionInput;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private Category selectedCategory;
    private FirebaseFirestore db;

    private static final String[] CURRENCIES = {"MYR", "USD", "EUR", "GBP", "JPY", "SGD"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupCurrencyDropdown();
        setupCategoryRecyclerView();
        setupTypeToggle();
        setupSaveButton();
    }

    private void initializeViews() {
        typeToggleGroup = findViewById(R.id.typeToggleGroup);
        amountInput = findViewById(R.id.amountInput);
        currencyDropdown = findViewById(R.id.currencyDropdown);
        descriptionInput = findViewById(R.id.descriptionInput);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);

        // Set default type to expense
        typeToggleGroup.check(R.id.expenseButton);
    }

    private void setupCurrencyDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CURRENCIES);
        currencyDropdown.setAdapter(adapter);
        currencyDropdown.setText(CURRENCIES[0], false);
    }

    private void setupCategoryRecyclerView() {
        List<Category> categories = createCategories();
        categoryAdapter = new CategoryAdapter(categories, this);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupTypeToggle() {
        typeToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                String type = checkedId == R.id.incomeButton ? "income" : "expense";
                categoryAdapter.filterCategories(type);
            }
        });
    }

    private void setupSaveButton() {
        findViewById(R.id.saveButton).setOnClickListener(v -> saveTransaction());
    }

    private List<Category> createCategories() {
        List<Category> categories = new ArrayList<>();
        
        // Income categories
        categories.add(new Category("salary", "Salary", "income", R.drawable.ic_salary, "#4CAF50"));
        categories.add(new Category("investment", "Investment", "income", R.drawable.ic_investment, "#8BC34A"));
        categories.add(new Category("bonus", "Bonus", "income", R.drawable.ic_bonus, "#CDDC39"));
        categories.add(new Category("gift", "Gift", "income", R.drawable.ic_gift, "#FFC107"));

        // Expense categories
        categories.add(new Category("food", "Food", "expense", R.drawable.ic_food, "#F44336"));
        categories.add(new Category("transport", "Transport", "expense", R.drawable.ic_transport, "#E91E63"));
        categories.add(new Category("shopping", "Shopping", "expense", R.drawable.ic_shopping, "#9C27B0"));
        categories.add(new Category("bills", "Bills", "expense", R.drawable.ic_bills, "#673AB7"));
        categories.add(new Category("entertainment", "Entertainment", "expense", R.drawable.ic_entertainment, "#3F51B5"));
        categories.add(new Category("health", "Health", "expense", R.drawable.ic_health, "#2196F3"));
        categories.add(new Category("education", "Education", "expense", R.drawable.ic_education, "#03A9F4"));
        categories.add(new Category("others", "Others", "expense", R.drawable.ic_others, "#607D8B"));

        return categories;
    }

    @Override
    public void onCategorySelected(Category category) {
        this.selectedCategory = category;
    }

    private void saveTransaction() {
        if (!validateInputs()) {
            return;
        }

        String type = typeToggleGroup.getCheckedButtonId() == R.id.incomeButton ? "income" : "expense";
        double amount = Double.parseDouble(amountInput.getText().toString());
        if (type.equals("expense")) {
            amount = -amount;
        }

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("amount", amount);
        transaction.put("currency", currencyDropdown.getText().toString());
        transaction.put("category", selectedCategory.getName());
        transaction.put("categoryId", selectedCategory.getId());
        transaction.put("description", descriptionInput.getText().toString());
        transaction.put("type", type);
        transaction.put("date", new Date());

        db.collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving transaction: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInputs() {
        if (amountInput.getText().toString().isEmpty()) {
            amountInput.setError("Amount is required");
            return false;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
