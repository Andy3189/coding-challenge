package com.hausding.challenge.model.api.coingecko

import com.hausding.challenge.model.api.Coin
import com.hausding.challenge.model.api.ConversionRate
import com.hausding.challenge.model.api.Currency
import com.hausding.challenge.model.api.ICoinDataSource
import com.hausding.challenge.model.api.coingecko.parser.CoinConversionParser
import com.hausding.challenge.model.api.coingecko.parser.CoinParser
import com.hausding.challenge.model.api.coingecko.parser.ConversionRateHistoryParser
import com.hausding.challenge.model.api.coingecko.parser.CurrencyParser
import com.squareup.moshi.*
import okio.Buffer
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Handler for CoinGecko API
 */
class CoinGeckoHandler: ICoinDataSource {
    companion object {
        private const val API_URL = "https://api.coingecko.com/api/v3"
        private const val COINS_URL = "/coins/list"
        private const val CURRENT_DATA_URL = "/simple/price"
        private const val CURRENCIES_URL = "/simple/supported_vs_currencies"
        private const val HISTORICAL_DATA_URL = "/coins/%s/market_chart"
        private const val PING_URL = "/ping"
    }

    /**
     * Function for api requests
     * @param requestUrl url for request
     * @param onSuccess Callback function for successful requests
     * @param onError Callback function for request failures
     */
    private fun sendRequest(requestUrl: String, onSuccess: (Buffer) -> Unit, onError: (Exception) -> Unit) {
        try {
            URL("$API_URL$requestUrl").run {
                openConnection().run {
                    this as HttpURLConnection
                    connectTimeout = TimeUnit.SECONDS.toMillis(30).toInt()
                    requestMethod = "GET"
                    connect()
                    if (responseCode == 200) {
                        val buffer = Buffer()
                        buffer.writeString(inputStream.bufferedReader().use { it.readText() }, Charsets.UTF_8)
                        onSuccess(buffer)
                    } else {
                        onError(Exception("$responseCode"))
                    }
                }
            }
        } catch (ex: Exception) {
            onError(ex)
        }
    }

    override fun getCoins(onSuccess: (List<Coin>) -> Unit, onError: (Exception) -> Unit) {
        sendRequest(
            COINS_URL,
            onSuccess = { buffer ->
                try {
                    onSuccess(CoinParser().fromJson(JsonReader.of(buffer)))
                } catch (ex: Exception) {
                    onError(ex)
                }
            },
            onError = onError)
    }

    override fun getCurrencies(onSuccess: (List<Currency>) -> Unit, onError: (Exception) -> Unit) {
        sendRequest(
            CURRENCIES_URL,
            onSuccess = { buffer ->
                try {
                    onSuccess(CurrencyParser().fromJson(JsonReader.of(buffer)))
                } catch (ex: Exception) {
                    onError(ex)
                }
            },
            onError = onError)
    }

    override fun getCurrentDataForCoin(
        coin: String,
        currency: String,
        onSuccess: (ConversionRate) -> Unit,
        onError: (Exception) -> Unit) {
        val requestUrl = CoinUrlBuilder(CURRENT_DATA_URL)
            .addParameter("ids", coin)
            .addParameter("vs_currencies", currency)
            .build()
        sendRequest(
            requestUrl,
            onSuccess = { buffer ->
                try {
                    onSuccess(CoinConversionParser().fromJson(JsonReader.of(buffer)))
                } catch (ex: Exception) {
                    onError(ex)
                }
            },
            onError = onError)
    }

    override fun getHistoricalDataForCoin(
        coin: String,
        currency: String,
        durationDays: Int,
        dailyDataOnly: Boolean,
        onSuccess: (List<ConversionRate>) -> Unit,
        onError: (Exception) -> Unit) {
        val requestUrlBuilder = CoinUrlBuilder(String.format(HISTORICAL_DATA_URL, coin))
            .addParameter("vs_currency", currency)
            .addParameter("days", "$durationDays")
        if (dailyDataOnly) {
            requestUrlBuilder.addParameter("interval", "daily")
        }
        sendRequest(
            requestUrlBuilder.build(),
            onSuccess = { buffer ->
                try {
                    onSuccess(ConversionRateHistoryParser().fromJson(JsonReader.of(buffer)))
                } catch (ex: Exception) {
                    onError(ex)
                }
            },
            onError = onError)
    }

    override fun checkSourceAvailable(
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        sendRequest(
            PING_URL,
            { onSuccess()},
            onError
        )
    }
}
