package com.hausding.challenge.model.api

import com.squareup.moshi.JsonClass
/**
 * Data class for currency data
 * @property id Currency id
 */
@JsonClass(generateAdapter = true)
data class Currency(val id: String)