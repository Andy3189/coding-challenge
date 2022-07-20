package com.hausding.challenge

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hausding.challenge.model.api.*
import com.hausding.challenge.model.api.coingecko.CoinGeckoHandler
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture

/**
 * Check CoinGecko api handler
 */
@RunWith(AndroidJUnit4::class)
class CoinGeckoHandlerTest {
    /**
     * Check whether current conversion rate for bitcoin to eur can be retrieved
     */
    @Test
    fun testCurrentData() {
        val currentRateFuture = CompletableFuture<ConversionRate?>()
        assertFalse(currentRateFuture.isDone)
        CoinGeckoHandler().getCurrentDataForCoin(
            "bitcoin",
            "eur",
            onSuccess =  {
                currentRateFuture.complete(it)
            },
            onError = {
                currentRateFuture.complete(null)
            }
        )
        assertTrue(currentRateFuture.isDone)
        assertNotNull(currentRateFuture.get())
    }

    /**
     * Check whether all coins can be retrieved
     */
    @Test
    fun testCoins() {
        val coinFuture = CompletableFuture<List<Coin>?>()
        assertFalse(coinFuture.isDone)
        CoinGeckoHandler().getCoins(
            onSuccess =  {
                coinFuture.complete(it)
            },
            onError = {
                coinFuture.complete(null)
            }
        )
        assertTrue(coinFuture.isDone)
        assertNotNull(coinFuture.get())
    }

    /**
     * Check whether all currencies can be retrieved
     */
    @Test
    fun testCurrencies() {
        val currencyFuture = CompletableFuture<List<Currency>?>()
        assertFalse(currencyFuture.isDone)
        CoinGeckoHandler().getCurrencies(
            onSuccess =  {
                currencyFuture.complete(it)
            },
            onError = {
                currencyFuture.complete(null)
            }
        )
        assertTrue(currencyFuture.isDone)
        assertNotNull(currencyFuture.get())
    }

    /**
     * Check whether historical rates for bitcoin to eur can be retrieved for last 14 days
     */
    @Test
    fun testHistoricalRates() {
        val coinFuture = CompletableFuture<List<ConversionRate>?>()
        assertFalse(coinFuture.isDone)
        CoinGeckoHandler().getHistoricalDataForCoin(
            "bitcoin",
            "eur",
            14,
            true,
            onSuccess =  {
                coinFuture.complete(it)
            },
            onError = {
                coinFuture.complete(null)
            }
        )
        assertTrue(coinFuture.isDone)
        assertNotNull(coinFuture.get())
    }

    /**
     * Check whether source available check works
     */
    @Test
    fun testSourceAvailable() {
        val availableFuture = CompletableFuture<Boolean>()
        assertFalse(availableFuture.isDone)
        CoinGeckoHandler().checkSourceAvailable(
            onSuccess = {
                availableFuture.complete(true)
            },
            onError = {
                availableFuture.complete(false)
            }
        )
        assertTrue(availableFuture.isDone)
        assertTrue(availableFuture.get())
    }
}