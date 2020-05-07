package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.cniekirk.traintimes.model.adapter.SingleToArray

data class JourneyPlannerResponse(
    @Json(name = "generatedTime")
    val generatedTime: String?,
    @Json(name = "nrsStatus")
    val nrsStatus: NrsStatus?,
    @SingleToArray
    @Json(name = "outwardJourney")
    val outwardJourney: List<Journey>?,
    @SingleToArray
    @Json(name = "inwardJourney")
    val inwardJourney: List<Journey>?,
    @Json(name = "response")
    val response: String?
)