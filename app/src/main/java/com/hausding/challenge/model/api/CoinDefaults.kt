package com.hausding.challenge.model.api

/**
 * Default parameters for CoinDataManager
 */
object CoinDefaults {
    const val CACHE_FILE_NAME = "cache.json"
    val DEFAULT_SELECTED_COIN = "bitcoin"
    val DEFAULT_SELECTED_CURRENCY = "eur"
    val DEFAULT_COINS = listOf<Coin>()
    val DEFAULT_CURRENCIES = listOf<Currency>()
    val DEFAULT_DURATION = 14
}