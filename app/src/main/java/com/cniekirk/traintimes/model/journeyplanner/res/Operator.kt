package com.cniekirk.traintimes.model.journeyplanner.res
import com.squareup.moshi.Json

data class Operator(
    @Json(name = "code")
    val code: String,
    @Json(name = "name")
    val name: String
)