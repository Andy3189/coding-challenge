package com.hausding.challenge.model.api

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Manager class for coin data
 * @property scope Scope for coroutines
 * @property dataSource Source for coin data requests
 * @property _selectedCoin MutableLiveData for selected coin id
 * @property selectedCoin LiveData for selected coin id
 * @property _selectedCurrency MutableLiveData for selected currency id
 * @property selectedCurrency LiveData for selected currency id
 * @property _coins MutableLiveData for Coin list
 * @property coins LiveData for Coin list
 * @property _currencies MutableLiveData for Currency list
 * @property currencies LiveData for Currency list
 * @property _rates MutableLiveData for ConversionRate list
 * @property rates LiveData for ConversionRate list
 * @property _syncDate MutableLiveData for last sync date
 * @property syncDate LiveData for last sync date
 * @property _errorBus MutableLiveData for last request errors
 * @property errorBus LiveData for last request errors
 * @property _selectedDuration MutableLiveData for selected duration till oldest conversion rate in days
 * @property selectedDuration LiveData for selected duration till oldest conversion rate in days
 * @property coinsLoaded Flag indicating successful coin list load
 * @property currenciesLoaded Flag indicating successful currency list load
 * @property historicalDataLoaded Flag indicating successful historical conversion rate list load
 */
object CoinDataManager {
    private var scope = MainScope()
    private var dataSource: ICoinDataSource? = null
    private var _selectedCoin = MutableLiveData<String>()
    val selectedCoin: LiveData<String>
        get() = _selectedCoin
    private var _selectedCurrency = MutableLiveData<String>()
    val selectedCurrency: LiveData<String>
        get() = _selectedCurrency
    private val _coins = MutableLiveData(listOf<Coin>())
    val coins: LiveData<List<Coin>>
        get() = _coins
    private val _currencies = MutableLiveData(listOf<Currency>())
    val currencies: LiveData<List<Currency>>
        get() = _currencies
    private val _rates = MutableLiveData<List<ConversionRate>>()
    val rates: LiveData<List<ConversionRate>>
        get() = _rates
    private var _syncDate = MutableLiveData<LocalDateTime?>()
    val syncDate: LiveData<LocalDateTime?>
        get() = _syncDate
    private var _errorBus = MutableLiveData<Exception>()
    val errorBus: LiveData<Exception>
        get() = _errorBus
    var _selectedDuration = MutableLiveData<Int>()
    val selectedDuration: LiveData<Int>
        get() = _selectedDuration
    private var coinsLoaded = false
    private var currenciesLoaded = false
    private var historicalDataLoaded = false

    /**
     * Load properties from cache or restore defaults in case of no/unreadable cache
     * @param context Context object for cache loading
     * @param loadCallback Callback function after loading cache
     */
    fun loadCache(context: Context, loadCallback: () -> Unit) {
        val cache = CoinDataCache.loadCache(context)
        MainScope().launch {
            cache?.let { cache ->
                _selectedCoin.value = cache.selectedCoin
                _selectedCurrency.value = cache.selectedCurrency
                _coins.value = cache.coins
                _currencies.value = cache.currencies
                _rates.value = cache.rates
                _syncDate.value = cache.syncDate
                _selectedDuration.value = cache.selectedDuration

            } ?: run {
                _selectedCoin.value = CoinDefaults.DEFAULT_SELECTED_COIN
                _selectedCurrency.value = CoinDefaults.DEFAULT_SELECTED_CURRENCY
                _coins.value = CoinDefaults.DEFAULT_COINS
                _currencies.value = CoinDefaults.DEFAULT_CURRENCIES
                _selectedDuration.value = CoinDefaults.DEFAULT_DURATION
            }
            loadCallback()
        }
    }

    /**
     * Function for installing new data sources for coin data
     * @param source Object implementing ICoinDataSource
     */
    fun installDataSource(source: ICoinDataSource) {
        dataSource = source
    }

    /**
     * Function for retrieving all coins from data source
     */
    private fun retrieveCoins() {
        dataSource?.getCoins(
            onSuccess = {
                scope.launch {
                    _coins.value = it
                    coinsLoaded = true
                }
            },
            onError = {
                _errorBus.postValue(it)
            }
        )
    }

    /**
     * Function for retrieving all currencies from data source
     */
    private fun retrieveCurrencies() {
        dataSource?.getCurrencies(
            onSuccess = {
                scope.launch {
                    _currencies.value = it
                    currenciesLoaded = true
                }
            },
            onError = {
                _errorBus.postValue(it)
            }
        )
    }

    /**
     * Function for retrieving historical conversion rates for coin to currency
     */
    private fun retrieveHistoricalData() {
        dataSource?.getHistoricalDataForCoin(
            selectedCoin.value!!,
            selectedCurrency.value!!,
            selectedDuration.value!!,
            true,
            onSuccess = { data ->
                val sortedData = data.sortedByDescending { it.timestamp }
                scope.launch {
                    _rates.value = sortedData
                    historicalDataLoaded = true
                }
            },
            onError = {
                _errorBus.postValue(it)
            }
        )
    }

    /**
     * Function for retrieving current conversion rate
     * @param context Context object for cache storing
     */
    fun retrieveCurrentRate(context: Context) {
        dataSource?.getCurrentDataForCoin(
            selectedCoin.value!!,
            selectedCurrency.value!!,
            onSuccess = {
                //TODO caching
                scope.launch {
                    val prevList = _rates.value?.toMutableList() ?: mutableListOf()
                    if (prevList.isEmpty()) {
                        prevList.add(it)
                    } else {
                        prevList[0] = it
                    }
                    _rates.value =  prevList
                    _syncDate.value = LocalDateTime.now()
                    if (coinsLoaded && currenciesLoaded && historicalDataLoaded) {
                        scope.launch(Dispatchers.IO) {
                            CoinDataCache.storeCache(
                                context,
                                CoinDataCache(
                                    syncDate.value!!,
                                    coins.value!!,
                                    currencies.value!!,
                                    rates.value!!,
                                    selectedCoin.value!!,
                                    selectedCurrency.value!!,
                                    selectedDuration.value!!
                                )
                            )
                        }
                    }
                }
            },
            onError = {
                _errorBus.postValue(it)
            }
        )
    }

    /**
     * Function for loading all coins, currencies and historical conversion rates
     */
    fun initialLoad() {
        retrieveCoins()
        retrieveCurrencies()
        retrieveHistoricalData()
    }

    fun checkAvailable(onAvailable: () -> Unit, onError: (Exception) -> Unit) {
        dataSource?.checkSourceAvailable(
            onSuccess = onAvailable,
            onError = onError)
    }
    /**
     * Clear stored conversion rates
     */
    private fun resetHistory() {
        _rates.value = listOf()
    }

    /**
     * Setter function for selected Coin
     * @param coinID New selected coin id
     */
    fun setSelectedCoin(coinID: String) {
        _selectedCoin.value = coinID
        resetHistory()
    }
    /**
     * Setter function for selected currency
     * @param coinID New selected currency id
     */
    fun setSelectedCurrency(currencyID: String) {
        _selectedCurrency.value = currencyID
        resetHistory()
    }
}