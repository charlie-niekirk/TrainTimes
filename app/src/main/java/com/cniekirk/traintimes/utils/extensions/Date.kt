package com.cniekirk.traintimes.utils.extensions

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

fun SimpleDateFormat.now(): String {
    return this.format(Date.from(Instant.now()))
}