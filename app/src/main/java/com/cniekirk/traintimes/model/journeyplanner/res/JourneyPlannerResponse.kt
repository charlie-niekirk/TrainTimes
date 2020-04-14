package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class JourneyPlannerResponse(
    @Json(name = "generatedTime")
    val generatedTime: String?,
    @Json(name = "nrsStatus")
    val nrsStatus: NrsStatus?,
    @Json(name = "outwardJourney")
    val outwardJourney: List<OutwardJourney>?,
    @Json(name = "response")
    val response: String?
)