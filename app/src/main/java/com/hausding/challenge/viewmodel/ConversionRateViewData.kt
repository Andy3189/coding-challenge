package com.hausding.challenge.viewmodel

import com.hausding.challenge.model.Utils
import com.hausding.challenge.model.api.ConversionRate

/**
 * Data class for conversion rate data
 * @property timestamp String with timestamp of conversion rate
 * @property rate String with conversion rate
 */
data class ConversionRateViewData(val timestamp: String, val rate: String) {
    constructor(rate: ConversionRate): this(Utils.dateFormatter.format(rate.timestamp), rate.rate)
}