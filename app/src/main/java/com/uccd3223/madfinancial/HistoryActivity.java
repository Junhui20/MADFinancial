package com.uccd3223.madfinancial;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout historyContainer;
    private List<String> historyEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Spinner filterSpinner = findViewById(R.id.filterSpinner);
        Button applyFilterButton = findViewById(R.id.applyFilterButton);
        historyContainer = findViewById(R.id.historyContainer);

        // Load history from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SavingGoals", MODE_PRIVATE);
        String goals = sharedPreferences.getString("goals", "");

        // Parse the history entries into a list
        historyEntries = new ArrayList<>(Arrays.asList(goals.split("\n\n")));

        // Populate the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.filter_options, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // Set up the Apply Filter button
        applyFilterButton.setOnClickListener(v -> {
            String selectedFilter = filterSpinner.getSelectedItem().toString();
            applyFilter(selectedFilter);
        });

        // Display initial history
        displayHistory(historyEntries);
    }

    private void applyFilter(String filter) {
        switch (filter) {
            case "Sort by Duration":
                Collections.sort(historyEntries, Comparator.comparing(this::extractDuration));
                break;
            case "Sort by Saving Target (Ascending)":
                Collections.sort(historyEntries, Comparator.comparing(this::extractSavingTarget));
                break;
            case "Sort by Saving Target (Descending)":
                Collections.sort(historyEntries, Comparator.comparing(this::extractSavingTarget).reversed());
                break;
            case "Sort Alphabetically":
                Collections.sort(historyEntries);
                break;
        }

        // Refresh the displayed history
        displayHistory(historyEntries);
    }

    private int extractDuration(String entry) {
        // Extract the duration value from the entry
        String[] lines = entry.split("\n");
        for (String line : lines) {
            if (line.startsWith("Duration:")) {
                String duration = line.replace("Duration:", "").trim();
                return Integer.parseInt(duration.replaceAll("[^0-9]", ""));
            }
        }
        return 0; // Default if no duration found
    }

    private double extractSavingTarget(String entry) {
        // Extract the saving target value from the entry
        String[] lines = entry.split("\n");
        for (String line : lines) {
            if (line.startsWith("Saving Target:")) {
                String target = line.replace("Saving Target:", "").replace("RM", "").trim();
                return Double.parseDouble(target);
            }
        }
        return 0; // Default if no saving target found
    }

    private void displayHistory(List<String> entries) {
        historyContainer.removeAllViews();

        for (String entry : entries) {
            if (!entry.trim().isEmpty()) {
                // Create a card layout with proper spacing
                LinearLayout cardLayout = new LinearLayout(this);
                LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardLayoutParams.setMargins(0, 16, 0, 16); // Add spacing between cards
                cardLayout.setLayoutParams(cardLayoutParams);
                cardLayout.setOrientation(LinearLayout.VERTICAL);
                cardLayout.setBackgroundResource(R.drawable.card_background); // Use the card background

                // Parse and display each line in the card
                String[] lines = entry.split("\n");
                for (String line : lines) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    textView.setPadding(24, 8, 24, 8);
                    textView.setText(line);

                    // Bold headers (e.g., "Goals:", "Saving Target:")
                    if (line.contains(":")) {
                        textView.setTypeface(null, Typeface.BOLD);
                    }

                    cardLayout.addView(textView);
                }

                // Add the card to the container
                historyContainer.addView(cardLayout);
            }
        }
    }
}
