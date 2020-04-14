package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class NrsStatus(
    @Json(name = "code")
    val code: String?,
    @Json(name = "description")
    val description: String?
)