package com.example.currencycalculator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RatesRepository(private val prefsManager: SharedPreferencesManager) {
    private val api = RetrofitClient.api

    suspend fun getRates(forceRefresh: Boolean = false): Map<String, Double>? {
        return withContext(Dispatchers.IO) {
            val cachedRates = prefsManager.getRates()

            if (cachedRates == null || forceRefresh) {
                try {
                    val response = api.getLatestRates("USD")
                    if (response.result == "success") {
                        prefsManager.saveRates(response.rates)
                        response.rates
                    } else {
                        cachedRates
                    }
                } catch (e: Exception) {
                    cachedRates
                }
            } else {
                cachedRates
            }
        }
    }
}
