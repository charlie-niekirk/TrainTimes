package com.cniekirk.traintimes.utils.extensions

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

fun SimpleDateFormat.now(): String {
    return this.format(Date.from(Instant.now()))
}

fun Instant.hoursFromNow(numHours: Long): Date {
    return Date.from(this.minus(numHours, ChronoUnit.HOURS))
}