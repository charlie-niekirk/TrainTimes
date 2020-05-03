package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class Timetable(
    @Json(name = "realtime")
    val realtime: Scheduled?,
    @Json(name = "scheduled")
    val scheduled: Scheduled?
)