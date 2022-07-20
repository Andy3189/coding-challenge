package com.hausding.challenge.model.api.coingecko.parser

import com.hausding.challenge.model.api.ConversionRate
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Json parser for reading response json for CoinGecko simple price request
 */
class CoinConversionParser {
    /**
     * Function for json parsing of conversion rate
     * @param reader Json reader for json token handling
     * @return ConversionRate object
     * @throws java.io.IOException Exception on unexpected json tokens
     * @throws com.squareup.moshi.JsonDataException Exception on missing key/value json tokens
     */
    @FromJson
    fun fromJson(reader: JsonReader): ConversionRate {
        reader.beginObject()
        reader.nextName() // skip coin name
        reader.beginObject()
        reader.nextName() // skip currency name
        val currRate = reader.nextDouble()
        reader.endObject()
        reader.endObject()
        return ConversionRate(ZoneId.ofOffset("UTC", ZoneOffset.UTC),"$currRate") //CoinGecko uses UTC+00 timezone
    }
}