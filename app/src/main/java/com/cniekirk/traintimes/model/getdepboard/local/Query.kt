package com.cniekirk.traintimes.model.getdepboard.local

import com.squareup.moshi.Json

data class Query(
    @Json(name = "fromCrs") val fromCrs: String,
    @Json(name = "fromName") val fromName: String,
    @Json(name = "toCrs") val toCrs: String? = null,
    @Json(name = "toName") val toName: String? = null
)