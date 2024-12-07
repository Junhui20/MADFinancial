package com.uccd3223.madfinancial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencySpinnerAdapter extends ArrayAdapter<String> {
    private final Map<String, String> currencyNames;

    public CurrencySpinnerAdapter(Context context, List<String> currencies) {
        super(context, R.layout.custom_spinner, currencies);
        currencyNames = getCurrencyFullNames();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.custom_spinner, parent, false);
        }

        String currency = getItem(position);

        TextView currencyCodeText = convertView.findViewById(R.id.currencyCodeText);
        TextView currencyNameText = convertView.findViewById(R.id.currencyNameText);

        currencyCodeText.setText(currency);
        currencyNameText.setText(currencyNames.get(currency));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private Map<String, String> getCurrencyFullNames() {
        Map<String, String> names = new HashMap<>();
        names.put("USD", "US Dollar");
        names.put("EUR", "Euro");
        names.put("GBP", "British Pound");
        names.put("JPY", "Japanese Yen");
        names.put("AUD", "Australian Dollar");
        names.put("CAD", "Canadian Dollar");
        names.put("CHF", "Swiss Franc");
        names.put("CNY", "Chinese Yuan");
        names.put("HKD", "Hong Kong Dollar");
        names.put("MYR", "Malaysia Ringgit");
        names.put("SGD", "Singapore Dollar");
        names.put("INR", "Indian Rupee");
        names.put("BRL", "Brazilian Real");
        names.put("RUB", "Russian Ruble");
        names.put("ZAR", "South African Rand");
        names.put("KRW", "South Korean Won");
        names.put("AED", "UAE Dirham");
        names.put("SAR", "Saudi Riyal");
        names.put("MXN", "Mexican Peso");
        names.put("NZD", "New Zealand Dollar");
        names.put("THB", "Thai Baht");
        return names;
    }
}