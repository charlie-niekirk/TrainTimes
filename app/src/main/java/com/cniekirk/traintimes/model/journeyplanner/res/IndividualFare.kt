package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class IndividualFare(
    @Json(name = "accompaniedPassengers")
    val accompaniedPassengers: AccompaniedPassengers?,
    @Json(name = "accompaniedPrices")
    val accompaniedPrices: AccompaniedPrices?,
    @Json(name = "passengers")
    val passengers: Passengers?,
    @Json(name = "prices")
    val prices: Prices?
)