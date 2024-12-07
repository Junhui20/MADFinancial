package com.uccd3223.madfinancial;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddTransactionActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText amountInput;
    private EditText descriptionInput;
    private Button dateButton;
    private Button timeButton;
    private SwitchMaterial recurringSwitch;
    private Button addImageButton;
    private ImageView imagePreview;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private Uri selectedImageUri;
    private Calendar calendar;
    private DatabaseHelper dbHelper;
    private MaterialButton expenseButton;
    private MaterialButton incomeButton;
    private boolean editMode = false;
    private String editTransactionId = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Check if we're in edit mode
        editMode = getIntent().getBooleanExtra("EDIT_MODE", false);
        editTransactionId = getIntent().getStringExtra("TRANSACTION_ID");

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        initializeViews();
        setupDateTimeButtons();
        setupImageButton();
        setupCategoryRecyclerView();
        setupTypeButtons();
        setupSaveButton();

        if (editMode && editTransactionId != null) {
            loadTransactionForEdit();
        }
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.nameInput);
        amountInput = findViewById(R.id.amountInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        recurringSwitch = findViewById(R.id.recurringSwitch);
        addImageButton = findViewById(R.id.addImageButton);
        imagePreview = findViewById(R.id.imagePreview);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        expenseButton = findViewById(R.id.expenseButton);
        incomeButton = findViewById(R.id.incomeButton);
    }

    private void setupTypeButtons() {
        expenseButton.setOnClickListener(v -> {
            expenseButton.setChecked(true);
            incomeButton.setChecked(false);
            categoryAdapter.filterCategories("expense");
        });

        incomeButton.setOnClickListener(v -> {
            incomeButton.setChecked(true);
            expenseButton.setChecked(false);
            categoryAdapter.filterCategories("income");
        });

        // Set initial state
        expenseButton.setChecked(true);
        incomeButton.setChecked(false);
        categoryAdapter.filterCategories("expense");
    }

    private void setupCategoryRecyclerView() {
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        List<Category> categories = new ArrayList<>();

        // Add expense categories
        categories.add(new Category("1", "expense", "Food", "#FF5722")); // Orange
        categories.add(new Category("2", "expense", "Transport", "#2196F3")); // Blue
        categories.add(new Category("3", "expense", "Shopping", "#E91E63")); // Pink
        categories.add(new Category("4", "expense", "Bills", "#FFC107")); // Amber
        categories.add(new Category("5", "expense", "Entertainment", "#9C27B0")); // Purple
        categories.add(new Category("6", "expense", "Health", "#4CAF50")); // Green
        categories.add(new Category("7", "expense", "Education", "#03A9F4")); // Light Blue
        categories.add(new Category("8", "expense", "Housing", "#795548")); // Brown
        categories.add(new Category("9", "expense", "Insurance", "#607D8B")); // Blue Grey
        categories.add(new Category("10", "expense", "Tax", "#FF9800")); // Orange
        categories.add(new Category("11", "expense", "Utilities", "#009688")); // Teal
        categories.add(new Category("12", "expense", "Groceries", "#8BC34A")); // Light Green
        categories.add(new Category("13", "expense", "Personal Care", "#FF4081")); // Pink A200
        categories.add(new Category("14", "expense", "Pets", "#7C4DFF")); // Deep Purple A200
        categories.add(new Category("15", "expense", "Gifts", "#FF6E40")); // Deep Orange A200
        categories.add(new Category("16", "expense", "Travel", "#00BCD4")); // Cyan
        categories.add(new Category("17", "expense", "Electronics", "#3F51B5")); // Indigo
        categories.add(new Category("18", "expense", "Fitness", "#CDDC39")); // Lime
        categories.add(new Category("19", "expense", "Others", "#757575")); // Grey

        // Add income categories
        categories.add(new Category("101", "income", "Salary", "#00BCD4")); // Cyan
        categories.add(new Category("102", "income", "Bonus", "#8BC34A")); // Light Green
        categories.add(new Category("103", "income", "Investment", "#FF9800")); // Orange
        categories.add(new Category("104", "income", "Gift", "#795548")); // Brown
        categories.add(new Category("105", "income", "Interest", "#009688")); // Teal
        categories.add(new Category("106", "income", "Rental", "#3F51B5")); // Indigo
        categories.add(new Category("107", "income", "Others", "#757575")); // Grey

        categoryAdapter = new CategoryAdapter(categories);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupSaveButton() {
        findViewById(R.id.saveButton).setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        if (!validateInputs()) {
            return;
        }

        String transactionId = editMode ? editTransactionId : UUID.randomUUID().toString();
        String type = incomeButton.isChecked() ? "income" : "expense";
        double amount = Double.parseDouble(amountInput.getText().toString());
        String name = nameInput.getText().toString();
        Category selectedCategory = categoryAdapter.getSelectedCategory();
        String description = descriptionInput.getText().toString();

        Transaction transaction = new Transaction(
            transactionId,
            type,
            amount,
            name,
            selectedCategory.getName(),
            selectedCategory.getName(),
            selectedCategory.getColor(),
            calendar.getTimeInMillis(),
            description,
            false,
            "",
            selectedImageUri != null ? selectedImageUri.toString() : null
        );

        if (editMode) {
            dbHelper.updateTransaction(transaction);
            Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.insertTransaction(transaction);
            Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void setupDateTimeButtons() {
        updateDateButtonText();
        updateTimeButtonText();

        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateButtonText();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
           calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateTimeButtonText();
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void updateDateButtonText() {
        dateButton.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateTimeButtonText() {
        timeButton.setText(timeFormat.format(calendar.getTime()));
    }

    private void setupImageButton() {
        addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void loadTransactionForEdit() {
        Transaction transaction = dbHelper.getTransactionById(editTransactionId);
        if (transaction != null) {
            // Set amount
            amountInput.setText(String.format(Locale.getDefault(), "%.2f", transaction.getAmount()));

            // Set type
            if (transaction.getType().equals("income")) {
                incomeButton.setChecked(true);
                expenseButton.setChecked(false);
                categoryAdapter.filterCategories("income");
            } else {
                expenseButton.setChecked(true);
                incomeButton.setChecked(false);
                categoryAdapter.filterCategories("expense");
            }

            // Set name
            nameInput.setText(transaction.getName());

            // Set date and time
            calendar.setTimeInMillis(transaction.getTimestamp());
            updateDateButtonText();
            updateTimeButtonText();

            // Set description
            descriptionInput.setText(transaction.getDescription());

            // Set image
            if (transaction.getImageUri() != null && !transaction.getImageUri().isEmpty()) {
                selectedImageUri = Uri.parse(transaction.getImageUri());
                imagePreview.setImageURI(selectedImageUri);
                imagePreview.setVisibility(View.VISIBLE);
            }

            // Set category (after filtering categories)
            String category = transaction.getCategory();
            categoryAdapter.setSelectedCategory(category);
        }
    }

    private boolean validateInputs() {
        String name = nameInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        Category selectedCategory = categoryAdapter.getSelectedCategory();

        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Please enter a name");
            return false;
        }

        if (TextUtils.isEmpty(amountStr)) {
            amountInput.setError("Please enter an amount");
            return false;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
