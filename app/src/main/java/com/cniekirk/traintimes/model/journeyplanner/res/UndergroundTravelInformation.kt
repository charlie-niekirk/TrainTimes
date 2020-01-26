package com.cniekirk.traintimes.model.journeyplanner.res
import com.squareup.moshi.Json

data class UndergroundTravelInformation(
    @Json(name = "message")
    val message: String
)