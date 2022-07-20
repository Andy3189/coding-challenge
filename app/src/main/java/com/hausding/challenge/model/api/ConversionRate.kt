package com.hausding.challenge.model.api

import com.squareup.moshi.JsonClass
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
/**
 * Data class for conversion rate data
 * @property timestamp Datetime of conversion rate
 * @property rate Conversion rate
 */
@JsonClass(generateAdapter = true)
data class ConversionRate (val timestamp: LocalDateTime, val rate: String) {
    constructor(timestamp: Long, zoneId: ZoneId, rate: String):this(LocalDateTime.from(Instant.ofEpochMilli(timestamp).atZone(zoneId)), rate)
    constructor(zoneId: ZoneId, rate: String):this(LocalDateTime.now(zoneId), rate)
}