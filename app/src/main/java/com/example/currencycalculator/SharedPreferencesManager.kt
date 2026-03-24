package com.example.currencycalculator

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "currency_prefs"
        private const val KEY_RATES = "rates"
        private const val KEY_LAST_FETCH_TIME = "last_fetch_time"
        private const val KEY_SOURCE_CURRENCY = "source_currency"
        private const val KEY_TARGET_CURRENCY = "target_currency"
    }

    fun saveRates(rates: Map<String, Double>) {
        val json = gson.toJson(rates)
        prefs.edit()
            .putString(KEY_RATES, json)
            .putLong(KEY_LAST_FETCH_TIME, System.currentTimeMillis())
            .apply()
    }

    fun getRates(): Map<String, Double>? {
        val json = prefs.getString(KEY_RATES, null) ?: return null
        val type = object : TypeToken<Map<String, Double>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getLastFetchTime(): Long {
        return prefs.getLong(KEY_LAST_FETCH_TIME, 0L)
    }

    fun saveSourceCurrency(currency: String) {
        prefs.edit().putString(KEY_SOURCE_CURRENCY, currency).apply()
    }

    fun getSourceCurrency(): String {
        return prefs.getString(KEY_SOURCE_CURRENCY, "EUR") ?: "EUR"
    }

    fun saveTargetCurrency(currency: String) {
        prefs.edit().putString(KEY_TARGET_CURRENCY, currency).apply()
    }

    fun getTargetCurrency(): String {
        return prefs.getString(KEY_TARGET_CURRENCY, "USD") ?: "USD"
    }
}
