package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class Operator(
    @Json(name = "code")
    val code: String?,
    @Json(name = "name")
    val name: String?
)