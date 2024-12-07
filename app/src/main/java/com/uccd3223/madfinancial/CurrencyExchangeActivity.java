package com.uccd3223.madfinancial;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyExchangeActivity extends AppCompatActivity {

    private EditText amountInput;
    private Spinner currencySpinner;
    private TextView exchangeRateText, convertedAmountText;
    private Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_exchange);


        // Set title and enable back button
        setTitle("Currency Exchange");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Views
        amountInput = findViewById(R.id.amountInput);
        currencySpinner = findViewById(R.id.currencySpinner);
        exchangeRateText = findViewById(R.id.exchangeRateText);
        convertedAmountText = findViewById(R.id.convertedAmountText);
        convertButton = findViewById(R.id.convertButton);

        // Populate Currency Spinner
        String[] currencies = {"USD", "EUR", "GBP", "JPY", "AUD", "CNY", "SGD", "THB", "IDR", "PHP", "VND", "TWD", "BND"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

        // Set Convert Button Listener
        convertButton.setOnClickListener(v -> {
            // Change button color temporarily
            convertButton.setBackgroundColor(getResources().getColor(R.color.light_purple)); // Define light purple in colors.xml
            new Handler().postDelayed(() -> convertButton.setBackgroundColor(getResources().getColor(R.color.light_blue)), 200); // Reset after 200ms

            // Fetch exchange rate and update UI
            fetchExchangeRate();
        });
    }

    private void fetchExchangeRate() {
        String targetCurrency = currencySpinner.getSelectedItem().toString();
        String apiUrl = "https://v6.exchangerate-api.com/v6/e731a9740b61a8237dde3fbb/latest/MYR";

        new Thread(() -> {
            try {
                // Open connection
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000); // 5 seconds timeout
                conn.setReadTimeout(5000); // 5 seconds timeout

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON
                    JSONObject jsonObject = new JSONObject(response.toString());
                    double exchangeRate = jsonObject.getJSONObject("conversion_rates").getDouble(targetCurrency);
                    runOnUiThread(() -> Toast.makeText(this, "Successful to fetch exchange rates!", Toast.LENGTH_SHORT).show());

                    // Update UI
                    runOnUiThread(() -> updateUI(exchangeRate));
                } else {
                    runOnUiThread(() -> Toast.makeText(CurrencyExchangeActivity.this, "Failed to fetch exchange rates. Response Code: " + responseCode, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CurrencyExchangeActivity.this, "Error: " + e.getMessage() + "\n\nUnable to connect to the internet, try using the phone!", Toast.LENGTH_LONG).show());

            }
        }).start();
    }


    private void updateUI(double exchangeRate) {
        exchangeRateText.setText("Exchange Rate: " + exchangeRate);

        // Calculate Converted Amount
        String amountStr = amountInput.getText().toString();
        String targetCurrency = currencySpinner.getSelectedItem().toString(); // Get selected currency
        if (!amountStr.isEmpty()) {
            double amount = Double.parseDouble(amountStr);
            double convertedAmount = amount * exchangeRate;
            convertedAmountText.setText("Converted Amount: " + targetCurrency + String.format("%.2f", convertedAmount));

            // Retract Keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
        }

    }


    public boolean onSupportNavigateUp() {
        // Handle back button press
        finish();
        return true;
    }
}
