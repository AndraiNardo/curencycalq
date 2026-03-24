package com.example.currencycalculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
    private val prefsManager = SharedPreferencesManager(application)
    private val repository = RatesRepository(prefsManager)

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates.asStateFlow()

    private val _sourceCurrency = MutableStateFlow(prefsManager.getSourceCurrency())
    val sourceCurrency: StateFlow<String> = _sourceCurrency.asStateFlow()

    private val _targetCurrency = MutableStateFlow(prefsManager.getTargetCurrency())
    val targetCurrency: StateFlow<String> = _targetCurrency.asStateFlow()

    private val _amount = MutableStateFlow("1.0")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _convertedAmount = MutableStateFlow("0.0")
    val convertedAmount: StateFlow<String> = _convertedAmount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchRates(forceRefresh = false)
    }

    fun fetchRates(forceRefresh: Boolean = true) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val fetchedRates = repository.getRates(forceRefresh)
            if (fetchedRates != null) {
                _rates.value = fetchedRates
                calculateConversion()
            } else {
                _error.value = "Failed to load exchange rates. Please check your connection."
            }
            _isLoading.value = false
        }
    }

    fun updateAmount(newAmount: String) {
        _amount.value = newAmount
        calculateConversion()
    }

    fun updateSourceCurrency(currency: String) {
        _sourceCurrency.value = currency
        prefsManager.saveSourceCurrency(currency)
        calculateConversion()
    }

    fun updateTargetCurrency(currency: String) {
        _targetCurrency.value = currency
        prefsManager.saveTargetCurrency(currency)
        calculateConversion()
    }

    fun swapCurrencies() {
        val temp = _sourceCurrency.value
        updateSourceCurrency(_targetCurrency.value)
        updateTargetCurrency(temp)
    }

    private fun calculateConversion() {
        val currentRates = _rates.value
        if (currentRates.isEmpty()) return

        val amountValue = _amount.value.toDoubleOrNull() ?: 0.0
        val sourceRate = currentRates[_sourceCurrency.value] ?: 1.0
        val targetRate = currentRates[_targetCurrency.value] ?: 1.0

        // Base currency for the rates is USD
        val amountInUsd = amountValue / sourceRate
        val converted = amountInUsd * targetRate

        _convertedAmount.value = String.format("%.4f", converted)
    }
}
