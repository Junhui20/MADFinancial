package com.uccd3223.madfinancial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyExchangeActivity extends BaseActivity {

    private static final String BASE_URL = "https://api.forexrateapi.com/v1/";
    private static final String API_KEY = "107ba04c1abbbbdcead462098750be65";
    private final int UPDATE_INTERVAL = 30000; // 30 seconds

    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private EditText amountEditText;
    private Button convertButton;
    private TextView resultTextView;
    private ImageButton backButton;

    private CurrencyApi api;
    private Map<String, Double> currentRates;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_exchange);
        setupToolbar("Currency Converter");

        amountEditText = findViewById(R.id.activity_currency_exchange_inputAmount);
        spinnerFrom = findViewById(R.id.activity_currency_exchange_sourceCurrencySpinner);
        spinnerTo = findViewById(R.id.activity_currency_exchange_targetCurrencySpinner);
        convertButton = findViewById(R.id.activity_currency_exchange_convertButton);
        resultTextView = findViewById(R.id.activity_currency_exchange_resultText);
        backButton = findViewById(R.id.backButton);

        // Set up back button
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                finish();
            });
        }

        // Initialize currency spinners with dummy data
        List<String> currencies = Arrays.asList(
                "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "HKD","MYR",
                "SGD", "INR", "BRL", "RUB", "ZAR", "KRW", "AED", "SAR", "MXN",
                "NZD", "THB"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Set default selections
        spinnerFrom.setSelection(0); // USD
        spinnerTo.setSelection(6);   // CNY

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build())
                .build();

        api = retrofit.create(CurrencyApi.class);

        // Start real-time fetching of exchange rates
        startFetchingRates();

        // Set up convert button click listener
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
            }
        });
    }

    private void startFetchingRates() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                fetchExchangeRates();
                handler.postDelayed(this, UPDATE_INTERVAL); // Schedule the next update
            }
        });
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (SecurityException e) {
            Log.e("CurrencyExchange", "Network state permission not granted", e);
            return false;
        } catch (Exception e) {
            Log.e("CurrencyExchange", "Error checking network state", e);
            return false;
        }
    }

    private void fetchExchangeRates() {
        if (!isNetworkAvailable()) {
            runOnUiThread(() -> {
                Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show();
                resultTextView.setText("No network connection");
            });
            return;
        }

        Call<CurrencyResponse> call = api.getExchangeRates(API_KEY, "MYR");
        Log.d("CurrencyExchange", "Fetching rates with API key: " + API_KEY);
        
        call.enqueue(new Callback<CurrencyResponse>() {
            @Override
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                Log.d("CurrencyExchange", "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    currentRates = response.body().getRates();
                    Log.d("CurrencyExchange", "Rates received: " + currentRates);
                    if (currentRates == null || currentRates.isEmpty()) {
                        runOnUiThread(() -> {
                            Toast.makeText(CurrencyExchangeActivity.this, 
                                "No exchange rates available", Toast.LENGTH_SHORT).show();
                            resultTextView.setText("No exchange rates available");
                        });
                    }
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        errorBody = "Error reading error body";
                    }
                    Log.e("CurrencyExchange", "Error response: " + errorBody);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(CurrencyExchangeActivity.this, 
                            "Failed to fetch exchange rates: " + response.code(), Toast.LENGTH_SHORT).show();
                        resultTextView.setText("Failed to fetch exchange rates");
                    });
                }
            }

            @Override
            public void onFailure(Call<CurrencyResponse> call, Throwable t) {
                Log.e("CurrencyExchange", "Network error", t);
                runOnUiThread(() -> {
                    Toast.makeText(CurrencyExchangeActivity.this, 
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    resultTextView.setText("Network error: Unable to fetch rates");
                });
            }
        });
    }

    private void convertCurrency() {
        if (currentRates == null) {
            Log.e("CurrencyExchange", "Current rates is null");
            resultTextView.setText("Exchange rates not available.");
            return;
        }

        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String fromCurrency = spinnerFrom.getSelectedItem().toString();
        String toCurrency = spinnerTo.getSelectedItem().toString();

        Log.d("CurrencyExchange", "Converting from " + fromCurrency + " to " + toCurrency);
        Log.d("CurrencyExchange", "Available rates: " + currentRates);

        Double fromRate = currentRates.get(fromCurrency);
        Double toRate = currentRates.get(toCurrency);

        Log.d("CurrencyExchange", "From rate: " + fromRate + ", To rate: " + toRate);

        if (fromRate != null && toRate != null) {
            double convertedAmount = amount * (toRate / fromRate);

            // Format the result
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedResult = df.format(convertedAmount);

            // Display the result
            String resultText = String.format("%s %s = %s %s", 
                amountStr, fromCurrency, formattedResult, toCurrency);
            resultTextView.setText(resultText);
        } else {
            Log.e("CurrencyExchange", "Conversion failed. fromRate: " + fromRate + ", toRate: " + toRate);
            resultTextView.setText("Unable to convert selected currencies");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
