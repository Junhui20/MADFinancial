package com.uccd3223.madfinancial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    private TextView historyTextView;

    private LinearLayout goalsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize container where we will display the goals
        goalsContainer = findViewById(R.id.goalsContainer);

        // Load saved goals from SharedPreferences
        loadSavedGoals();
    }

    private void loadSavedGoals() {
        // Retrieve the saved goals from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SavingGoals", Context.MODE_PRIVATE);
        String savedGoals = sharedPreferences.getString("goals", "");

        // Check if there are saved goals
        if (savedGoals.isEmpty()) {
            Toast.makeText(this, "No saving goals found.", Toast.LENGTH_SHORT).show();
            goalsContainer.addView(createGoalCard("No saving goals recorded.", "", "", ""));
        } else {
            // Split the goals into individual entries and display them
            String[] goals = savedGoals.split("\n\n");  // Goals are separated by two newlines
            for (String goal : goals) {
                String[] goalDetails = goal.split("\n");
                if (goalDetails.length == 4) {
                    String goalName = goalDetails[0].replace("Goal: ", "");
                    String savingGoal = goalDetails[1].replace("Saving Target: RM ", "");
                    String currentSaving = goalDetails[2].replace("Current Saving: RM ", "");
                    String duration = goalDetails[3].replace("Duration: ", "");

                    // Add each goal to the container
                    goalsContainer.addView(createGoalCard(goalName, duration, savingGoal, currentSaving));
                }
            }
        }
    }

    private TextView createGoalCard(String goalName, String duration, String savingGoal, String currentSaving) {
        // Create a new TextView for each goal
        TextView goalCard = new TextView(this);
        goalCard.setText("Goal: " + goalName + "\n" +
                "Duration: " + duration + "\n" +
                "Saving Target: RM " + savingGoal + "\n" +
                "Current Saving: RM " + currentSaving);
        goalCard.setBackgroundResource(R.drawable.card_background);  // Set background to the card drawable
        goalCard.setPadding(16, 16, 16, 16);  // Add some padding for better visuals
        goalCard.setTextSize(14);  // Optional: Set text size

        // Add space between containers
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);  // Add margin at the bottom (space between containers)
        goalCard.setLayoutParams(params);  // Set the layout params with margin

        return goalCard;
    }



}
