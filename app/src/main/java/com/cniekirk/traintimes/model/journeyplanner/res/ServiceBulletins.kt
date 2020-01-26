package com.cniekirk.traintimes.model.journeyplanner.res
import com.squareup.moshi.Json

data class ServiceBulletins(
    @Json(name = "alert")
    val alert: String,
    @Json(name = "cleared")
    val cleared: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "disruption")
    val disruption: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "url")
    val url: Any
)