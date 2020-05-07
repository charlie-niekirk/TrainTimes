package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep
import com.cniekirk.traintimes.model.adapter.SingleToArray

data class Journey(
    @Json(name = "destination")
    val destination: String?,
    @SingleToArray
    @Json(name = "fare")
    val fare: List<Fare>?,
    @Json(name = "id")
    val id: String?,
    @SingleToArray
    @Json(name = "leg")
    val leg: List<Leg>?,
    @Json(name = "origin")
    val origin: String?,
    @Json(name = "realtimeClassification")
    val realtimeClassification: String?,
    @SingleToArray
    @Json(name = "serviceBulletins")
    val serviceBulletins: List<ServiceBulletin>?,
    @Json(name = "timetable")
    val timetable: Timetable?,
    @Json(name = "vstpService")
    val vstpService: String?
)