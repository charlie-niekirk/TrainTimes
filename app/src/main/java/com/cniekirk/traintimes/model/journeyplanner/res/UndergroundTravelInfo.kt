package com.cniekirk.traintimes.model.journeyplanner.res

import com.squareup.moshi.Json

data class UndergroundTravelInfo(
    @Json(name = "message")
    val message: String?
)