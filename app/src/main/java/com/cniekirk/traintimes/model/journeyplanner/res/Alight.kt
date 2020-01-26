package com.cniekirk.traintimes.model.journeyplanner.res
import com.squareup.moshi.Json

data class Alight(
    @Json(name = "crsCode")
    val crsCode: String,
    @Json(name = "stationType")
    val stationType: String
)