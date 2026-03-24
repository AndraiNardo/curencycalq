package com.example.currencycalculator

import com.google.gson.annotations.SerializedName

data class ExchangeRateResponse(
    @SerializedName("result") val result: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("rates") val rates: Map<String, Double>
)
