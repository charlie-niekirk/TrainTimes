package com.cniekirk.traintimes.model.journeyplanner.req

import com.squareup.moshi.Json

data class JourneyPlanRequest(
    @Json(name = "departTime") val departTime: String,
    @Json(name = "adults") val adults: Int? = 0,
    @Json(name = "children") val children: Int? = 0,
    @Json(name = "railcards") val railcards: List<Railcard>? = null,
    @Json(name = "returnTime") val returnTime: String? = null,
    @Json(name = "directOnly") val directOnly: Boolean = false
)