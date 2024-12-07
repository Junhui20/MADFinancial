package com.uccd3223.madfinancial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

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


public class MainActivity extends AppCompatActivity {



    private static final String BASE_URL = "https://api.forexrateapi.com/v1/";
    private static final String API_KEY = "d4e755607ada5e3e0a675e1b27283b88";
    private final int UPDATE_INTERVAL = 30000; // 30 seconds

    private Spinner sourceCurrency;
    private Spinner targetCurrency;
    private EditText inputAmount;
    private TextView resultText;

    private CurrencyApi api;
    private Map<String, Double> currentRates;
    private Handler handler = new Handler(Looper.getMainLooper());




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputAmount = findViewById(R.id.inputAmount);
        sourceCurrency = findViewById(R.id.sourceCurrencySpinner);
        targetCurrency = findViewById(R.id.targetCurrencySpinner);
        Button convertButton = findViewById(R.id.convertButton);
        resultText = findViewById(R.id.resultText);

        // Initialize currency spinners with dummy data
        List<String> currencies = Arrays.asList(
                "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "HKD","MYR",
                "SGD", "INR", "BRL", "RUB", "ZAR", "KRW", "AED", "SAR", "MXN",
                "NZD", "THB"
        );

        CurrencySpinnerAdapter currencyAdapter = new CurrencySpinnerAdapter(
                this,
                currencies
        );

        sourceCurrency.setAdapter(currencyAdapter);
        targetCurrency.setAdapter(currencyAdapter);

        // Optional: Set default selections
        sourceCurrency.setSelection(currencies.indexOf("USD"));
        targetCurrency.setSelection(currencies.indexOf("EUR"));

        sourceCurrency.setDropDownVerticalOffset(0);
        targetCurrency.setDropDownVerticalOffset(0);






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

        // Conversion button click listener
        convertButton.setOnClickListener(v -> convertCurrency());
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
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void fetchExchangeRates() {
        if (!isNetworkAvailable()) {
            resultText.setText("No network connection");
            return;
        }

        Call<CurrencyResponse> call = api.getExchangeRates(API_KEY, "USD");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Simply store the rates without logging
                    currentRates = response.body().getRates();
                } else {
                    // Simple error handling
                    resultText.setText("Failed to fetch exchange rates");
                }
            }

            @Override
            public void onFailure(Call<CurrencyResponse> call, Throwable t) {
                // Simplified error message
                resultText.setText("Network error: Unable to fetch rates");
            }
        });
    }




    @SuppressLint("DefaultLocale")
    private void convertCurrency() {
        if (currentRates == null) {
            resultText.setText("Exchange rates not available.");
            return;
        }

        try {
            double amount = Double.parseDouble(inputAmount.getText().toString());
            String fromCurrency = sourceCurrency.getSelectedItem().toString();
            String toCurrency = targetCurrency.getSelectedItem().toString();

            Double fromRate = currentRates.get(fromCurrency);
            Double toRate = currentRates.get(toCurrency);

            if (fromRate != null && toRate != null) {
                double convertedAmount = amount * (toRate / fromRate);
                String conversionDetails = String.format(
                        "Amount: %.2f %s\n" +
                                "From Currency: %s\n" +
                                "From Rate: %.4f\n" +
                                "To Currency: %s\n" +
                                "To Rate: %.4f\n" +
                                "Converted Amount: %.2f %s",
                        amount, fromCurrency,
                        fromCurrency, fromRate,
                        toCurrency, toRate,
                        convertedAmount, toCurrency
                );

                resultText.setText(conversionDetails);
            } else {
                resultText.setText("Unable to convert selected currencies");
            }
        } catch (NumberFormatException e) {
            resultText.setText("Please enter a valid number");
        } catch (Exception e) {
            resultText.setText("Conversion error");
        }
    }

}
