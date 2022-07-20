package com.hausding.challenge.model.api.coingecko

/**
 * Builder for request urls
 */
class CoinUrlBuilder(private var requestURL: String) {
    private var firstParameter = true

    /**
     * Builder function for adding parameters to url
     * @param key Key of parameter
     * @param value Value of parameter
     * @return CoinUrlBuilder object
     */
    fun addParameter(key: String, value: String): CoinUrlBuilder {
        if (firstParameter) {
            requestURL += "?"
            firstParameter = false
        } else {
            requestURL += "&"
        }
        requestURL += "$key=$value"
        return this
    }

    /**
     * Function for returning the request url with added parameters
     * @return String with request url with parameters
     */
    fun build(): String {
        return requestURL
    }
}