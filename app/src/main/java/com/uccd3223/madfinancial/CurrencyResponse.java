package com.uccd3223.madfinancial;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class CurrencyResponse {
    private boolean success;
    private String base;
    private Map<String, Double> rates;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}

