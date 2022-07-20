package com.hausding.challenge.model.api.coingecko.parser

import com.hausding.challenge.model.api.Coin
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader

/**
 * Json parser for reading response json for CoinGecko coins list request
 */
class CoinParser {
    /**
     * Function for json parsing of coins list
     * @param reader Json reader for json token handling
     * @return List of Coin objects
     * @throws java.io.IOException Exception on unexpected json tokens
     * @throws com.squareup.moshi.JsonDataException Exception on missing key/value json tokens
     */
    @FromJson
    fun fromJson(reader: JsonReader): List<Coin> {
        reader.beginArray()
        val coinList = mutableListOf<Coin>()
        while (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
            reader.beginObject()
            reader.nextName()
            val coinID = reader.nextString()
            reader.nextName()
            reader.nextString() //skip coin symbol
            reader.nextName()
            reader.nextString() //skip coin name
            coinList.add(Coin(coinID))
            reader.endObject()
        }
        reader.endArray()
        return coinList
    }
}