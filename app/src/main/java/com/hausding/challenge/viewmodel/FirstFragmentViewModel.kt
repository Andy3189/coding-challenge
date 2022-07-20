package com.hausding.challenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hausding.challenge.model.Utils
import com.hausding.challenge.model.api.CoinDataManager

/**
 * ViewModel for first fragment
 * @property coins LiveData with list of coins
 * @property currencies LiveData with list of currencies
 * @property rates LiveData with list of historical conversion rates
 * @property selectedCoin LiveData with selected coin id
 * @property selectedCurrency LiveData with selected currency id
 * @property syncDate LiveData with last sync date
 * @property errorBus LiveData with last request error
 */
class FirstFragmentViewModel: ViewModel() {
    /**
     * Function for selecting new coin id
     * @param position Index of new coin id
     */
    fun selectCoin(position: Int) {
        CoinDataManager.setSelectedCoin(coins.value!![position])
    }

    /**
     * Function for selecting new currency id
     * @param position Index of new currency id
     */
    fun selectCurrency(position: Int) {
        CoinDataManager.setSelectedCurrency(currencies.value!![position])
    }

    val coins: LiveData<List<String>> = Transformations.map(CoinDataManager.coins) { coins -> coins.map { it.id }}
    val currencies: LiveData<List<String>> = Transformations.map(CoinDataManager.currencies) { currencies -> currencies.map { it.id }}
    val rates = Transformations.map(CoinDataManager.rates) { rates -> rates.map { ConversionRateViewData(it) }}
    val selectedCoin = CoinDataManager.selectedCoin
    val selectedCurrency = CoinDataManager.selectedCurrency
    val syncDate: LiveData<String?> = Transformations.map(CoinDataManager.syncDate) { syncDate -> syncDate?.let { Utils.dateFormatter.format(it) } }
    val errorBus = CoinDataManager.errorBus
}