package com.cniekirk.traintimes.model.journeyplanner.res
import com.squareup.moshi.Json

data class Scheduled(
    @Json(name = "arrival")
    val arrival: String,
    @Json(name = "departure")
    val departure: String
)