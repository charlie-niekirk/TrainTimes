package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class ServiceBulletin(
    @Json(name = "alert")
    val alert: String?,
    @Json(name = "cleared")
    val cleared: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "disruption")
    val disruption: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "url")
    val url: String?
)