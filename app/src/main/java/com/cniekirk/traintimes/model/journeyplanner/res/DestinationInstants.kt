package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class DestinationInstants(
    @Json(name = "scheduledTime")
    val scheduledTime: String?,
    @Json(name = "stationCRS")
    val stationCRS: String?
)