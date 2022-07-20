package com.hausding.challenge.model

import java.time.format.DateTimeFormatter

/**
 * Utils class for common functions
 */
object Utils {
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
}