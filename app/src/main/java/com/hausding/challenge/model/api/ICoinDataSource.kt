package com.hausding.challenge.model.api

/**
 * Interface for coin data sources
 */
interface ICoinDataSource {
    /**
     * Function for retrieving all coins from source
     * @param onSuccess Callback function on successful retrieval
     * @param onError Callback function on error
     */
    fun getCoins(onSuccess: (List<Coin>) -> Unit, onError: (Exception) -> Unit)
    /**
     * Function for retrieving all currencies from source
     * @param onSuccess Callback function on successful retrieval
     * @param onError Callback function on error
     */
    fun getCurrencies(onSuccess: (List<Currency>) -> Unit, onError: (Exception) -> Unit)
    /**
     * Function for retrieving current coin to currency conversion rate from source
     * @param coin Coin ID
     * @param currency Currency ID
     * @param onSuccess Callback function on successful retrieval
     * @param onError Callback function on error
     */
    fun getCurrentDataForCoin(coin: String, currency: String, onSuccess: (ConversionRate) -> Unit, onError: (Exception) -> Unit)
    /**
     * Function for retrieving historical coin to currency conversion rates from source
     * @param coin Coin ID
     * @param currency Currency ID
     * @param durationDays Days till oldest conversion rate
     * @param dailyDataOnly Flag whether only one data set per day should be retrieved, except for current day
     * @param onSuccess Callback function on successful retrieval
     * @param onError Callback function on error
     */
    fun getHistoricalDataForCoin(coin: String, currency: String, durationDays: Int, dailyDataOnly: Boolean, onSuccess: (List<ConversionRate>) -> Unit, onError: (Exception) -> Unit)
    /**
     * Function for checking whether source is available
     * @param onSuccess Callback function on source available
     * @param onError Callback function on error
     */
    fun checkSourceAvailable(onSuccess: () -> Unit, onError: (Exception) -> Unit)
}