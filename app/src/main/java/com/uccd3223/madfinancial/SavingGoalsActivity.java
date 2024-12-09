package com.uccd3223.madfinancial;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SavingGoalsActivity extends BaseActivity {

    private EditText goalInput, savingGoalInput, durationInput, currentSavingInput;
    private TextView progressText, recommendedSavingText;
    private ProgressBar progressBar;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_goals);
        setupToolbar("Saving Goals");

        // Initialize views
        goalInput = findViewById(R.id.editTextGoals);
        durationInput = findViewById(R.id.editTextDuration);
        savingGoalInput = findViewById(R.id.editTextSavingGoals);
        currentSavingInput = findViewById(R.id.editTextCurrentSaving);
        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        recommendedSavingText = findViewById(R.id.recommendedSavingText);

        selectedDate = Calendar.getInstance();

        // Make durationInput non-editable, show Date Picker on click
        durationInput.setFocusable(false);
        durationInput.setOnClickListener(v -> showDatePicker());

        // Add "RM" prefix to savingGoalInput
        savingGoalInput.addTextChangedListener(new TextWatcher() {
            boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;

                String input = s.toString();
                if (!input.startsWith("RM")) {
                    isUpdating = true;
                    savingGoalInput.setText("RM");
                    savingGoalInput.setSelection(2);
                    isUpdating = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add TextWatcher to currentSavingInput to update progress dynamically
        currentSavingInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateProgress();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add TextWatcher to savingGoalInput to update progress dynamically
        savingGoalInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateProgress();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save button functionality
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String goalText = goalInput.getText().toString().trim();
            String savingGoalText = savingGoalInput.getText().toString().trim().replace("RM", "");
            String durationText = durationInput.getText().toString().trim();
            String currentSavingText = currentSavingInput.getText().toString().trim().replace("RM", "");

            // Check for empty values
            if (goalText.isEmpty()) {
                goalInput.setError("Please enter your goal!");
                return;
            }

            if (savingGoalText.isEmpty() || !savingGoalText.matches("\\d+(\\.\\d{1,2})?")) {
                savingGoalInput.setError("Please enter a valid saving goal!");
                return;
            }

            if (currentSavingText.isEmpty() || !currentSavingText.matches("\\d+(\\.\\d{1,2})?")) {
                currentSavingInput.setError("Please enter your current savings!");
                return;
            }

            if (durationText.isEmpty()) {
                durationInput.setError("Please select a duration!");
                return;
            }

            // Save data in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("SavingGoals", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Format and save the new entry
            String existingGoals = sharedPreferences.getString("goals", "");
            String newEntry = "Goal: " + goalText + "\nSaving Target: RM " + savingGoalText + "\nCurrent Saving: RM " + currentSavingText +
                    "\nDuration: " + durationText + "\n\n";
            editor.putString("goals", existingGoals + newEntry);
            editor.apply();

            // Notify user and reset inputs
            goalInput.setText("");
            savingGoalInput.setText("RM");
            currentSavingInput.setText("");
            durationInput.setText("");
            Toast.makeText(SavingGoalsActivity.this, "Goal saved successfully!", Toast.LENGTH_SHORT).show();

            // Finish the activity and go back to the HistoryActivity
            finish();
        });

        // Handle history button click
        ImageButton historyButton = findViewById(R.id.button);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(SavingGoalsActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            selectedDate.set(selectedYear, selectedMonth, selectedDay);
            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            durationInput.setText(date); // Display selected date

            calculateProgress(); // Update progress and recommended saving when date is selected
        }, year, month, day);

        // Set the minimum date to today
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void calculateProgress() {
        String savingGoalStr = savingGoalInput.getText().toString().replace("RM", "").trim();
        String currentSavingStr = currentSavingInput.getText().toString().replace("RM", "").trim();

        if (!savingGoalStr.isEmpty() && !currentSavingStr.isEmpty()) {
            try {
                double savingGoal = Double.parseDouble(savingGoalStr);
                double currentSaving = Double.parseDouble(currentSavingStr);

                // Calculate progress percentage
                double progressPercentage = (currentSaving / savingGoal) * 100;
                progressBar.setProgress((int) progressPercentage); // Update progress bar

                // Display progress message
                double amountLeft = savingGoal - currentSaving;
                long daysLeft = getDaysLeft(); // Get days left

                if (amountLeft > 0) {
                    progressText.setText(String.format("%.2f%% completed, you still have RM %.2f to reach the goal! You have %d day(s) left.", progressPercentage, amountLeft, daysLeft));
                } else {
                    progressText.setText("100% completed, congratulations! You have achieved your saving goal!");
                }

                // Calculate recommended saving per day
                if (daysLeft > 0) {
                    double recommendedSaving = amountLeft / daysLeft;
                    recommendedSavingText.setText(String.format("Recommended saving: RM %.2f per day", recommendedSaving));
                } else {
                    recommendedSavingText.setText("");
                }
            } catch (NumberFormatException e) {
                progressText.setText("Invalid input. Please enter valid values.");
                recommendedSavingText.setText("");
            }
        } else {
            progressText.setText("Enter your saving goal and current saving to see progress.");
            progressBar.setProgress(0);
            recommendedSavingText.setText("");
        }
    }

    private long getDaysLeft() {
        Calendar today = Calendar.getInstance();
        long diffInMillis = selectedDate.getTimeInMillis() - today.getTimeInMillis();
        return diffInMillis > 0 ? (diffInMillis / (1000 * 60 * 60 * 24)) + 1 : 0;
    }
}
