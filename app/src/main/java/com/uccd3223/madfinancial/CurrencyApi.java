package com.uccd3223.madfinancial;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrencyApi {
    @GET("latest")
    Call<CurrencyResponse> getExchangeRates(
            @Query("api_key") String apiKey,
            @Query("base") String baseCurrency
    );
}