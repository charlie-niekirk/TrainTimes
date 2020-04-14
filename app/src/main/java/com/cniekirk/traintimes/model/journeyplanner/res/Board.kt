package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class Board(
    @Json(name = "crsCode")
    val crsCode: String?,
    @Json(name = "stationType")
    val stationType: String?
)