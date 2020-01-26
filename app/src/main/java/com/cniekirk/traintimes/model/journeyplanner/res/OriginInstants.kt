package com.cniekirk.traintimes.model.journeyplanner.res
import com.squareup.moshi.Json

data class OriginInstants(
    @Json(name = "scheduledTime")
    val scheduledTime: String,
    @Json(name = "stationCRS")
    val stationCRS: String
)