package com.hausding.challenge.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Json adapter for reading/storing LocalDateTime objects into json compatible strings
 */
class LocalDateTimeAdapter {
    /**
     * Function for converting LocalDateTime into String
     * @param value LocalDateTime object
     * @return String with formatted LocalDateTime according to DATE_FORMATTER
     */
    @ToJson
    fun toJson(value: LocalDateTime): String {
        return DATE_FORMATTER.format(value)
    }
    /**
     * Function for converting LocalDateTime into String
     * @param value String with LocalDateTime data
     * @return LocalDateTime object
     * @throws java.time.DateTimeException on invalid Strings
     */
    @FromJson
    fun fromJson(value: String): LocalDateTime {
        return LocalDateTime.from(DATE_FORMATTER.parse(value))
    }

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME
    }
}