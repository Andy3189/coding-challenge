package com.hausding.challenge.model.api.coingecko.parser

import com.hausding.challenge.model.api.Currency
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader

/**
 * Json parser for reading response json for CoinGecko supported_vs_currencies request
 */
class CurrencyParser {
    /**
     * Function for json parsing of currency list
     * @param reader Json reader for json token handling
     * @return List of Currency objects
     * @throws java.io.IOException Exception on unexpected json tokens
     * @throws com.squareup.moshi.JsonDataException Exception on missing key/value json tokens
     */
    @FromJson
    fun fromJson(reader: JsonReader): List<Currency> {
        reader.beginArray()
        val currencyList = mutableListOf<Currency>()
        while (reader.peek() == JsonReader.Token.STRING) {
            currencyList.add(Currency(reader.nextString()))
        }
        reader.endArray()
        return currencyList
    }
}