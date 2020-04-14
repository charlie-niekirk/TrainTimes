package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class Scheduled(
    @Json(name = "arrival")
    val arrival: String?,
    @Json(name = "departure")
    val departure: String?
)