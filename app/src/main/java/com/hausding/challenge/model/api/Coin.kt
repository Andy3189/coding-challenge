package com.hausding.challenge.model.api

import com.squareup.moshi.JsonClass

/**
 * Data class for Coin data
 * @property id Coin id
 */
@JsonClass(generateAdapter = true)
data class Coin(val id: String)