package com.cniekirk.traintimes.model.track.msg

import com.squareup.moshi.Json

data class TS(
    @Json(name = "header")
    val TSHeader: TSHeader?,
    @Json(name = "Location")
    val location: List<Location>?
)