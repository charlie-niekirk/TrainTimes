package com.cniekirk.traintimes.model.journeyplanner.res
import com.cniekirk.traintimes.model.journeyplanner.res.Scheduled
import com.squareup.moshi.Json

data class Timetable(
    @Json(name = "realtime")
    val realtime: Any,
    @Json(name = "scheduled")
    val scheduled: Scheduled
)