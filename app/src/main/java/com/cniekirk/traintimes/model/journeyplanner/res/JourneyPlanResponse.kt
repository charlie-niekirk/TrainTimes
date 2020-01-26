package com.cniekirk.traintimes.model.journeyplanner.res

import com.squareup.moshi.Json

data class JourneyPlanResponse(
    @Json(name = "generatedTime")
    val generatedTime: String,
    @Json(name = "outwardJourney")
    val outwardJourney: List<OutwardJourney>,
    @Json(name = "response")
    val response: String
)