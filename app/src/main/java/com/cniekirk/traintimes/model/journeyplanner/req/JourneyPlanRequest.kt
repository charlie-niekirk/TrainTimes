package com.cniekirk.traintimes.model.journeyplanner.req

import com.squareup.moshi.Json

data class JourneyPlanRequest(
    @Json(name = "departTime") val departTime: String
)