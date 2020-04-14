package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class AccompaniedPrices(
    @Json(name = "adult")
    val adult: String?,
    @Json(name = "child")
    val child: String?
)