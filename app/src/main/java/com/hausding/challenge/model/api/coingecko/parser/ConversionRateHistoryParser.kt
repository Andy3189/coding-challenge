package com.hausding.challenge.model.api.coingecko.parser

import com.hausding.challenge.model.api.ConversionRate
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Json parser for reading response json for CoinGecko coins market chart request
 */
class ConversionRateHistoryParser {
    /**
     * Function for json parsing of conversion rates
     * @param reader Json reader for json token handling
     * @return List of ConversionRate objects
     * @throws java.io.IOException Exception on unexpected json tokens
     * @throws com.squareup.moshi.JsonDataException Exception on missing key/value json tokens
     */
    @FromJson
    fun fromJson(reader: JsonReader): List<ConversionRate> {
        reader.beginObject()
        reader.nextName() // prices key
        reader.beginArray()
        val rateList = mutableListOf<ConversionRate>()
        while (reader.peek() == JsonReader.Token.BEGIN_ARRAY) {
            reader.beginArray()
            val timestamp = reader.nextLong()
            val rate = reader.nextDouble()
            rateList.add(ConversionRate(timestamp, ZoneId.ofOffset("UTC", ZoneOffset.UTC), "$rate")) //CoinGecko uses UTC+00 timezone
            reader.endArray()
        }
        reader.endArray()
        reader.nextName() //skip marketcap
        reader.skipValue() //skip marketcap value
        reader.nextName() //skip volume
        reader.skipValue() //skip volume value
        reader.endObject()
        return rateList
    }
}