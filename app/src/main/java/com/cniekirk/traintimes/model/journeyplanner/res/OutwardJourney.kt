package com.cniekirk.traintimes.model.journeyplanner.res
import com.squareup.moshi.Json

data class OutwardJourney(
    @Json(name = "destination")
    val destination: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "leg")
    val leg: List<Leg>,
    @Json(name = "origin")
    val origin: String,
    @Json(name = "realtimeClassification")
    val realtimeClassification: String,
    @Json(name = "serviceBulletins")
    val serviceBulletins: ServiceBulletins,
    @Json(name = "timetable")
    val timetable: Timetable,
    @Json(name = "vstpService")
    val vstpService: String
)