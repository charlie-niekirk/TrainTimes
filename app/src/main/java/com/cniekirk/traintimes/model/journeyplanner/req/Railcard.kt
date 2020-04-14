package com.cniekirk.traintimes.model.journeyplanner.req

import com.squareup.moshi.Json

data class Railcard(
    @Json(name = "code") val code: String,
    @Json(name = "count") val count: Int
)