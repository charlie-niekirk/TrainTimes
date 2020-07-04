package com.cniekirk.traintimes.model.track.msg

import com.squareup.moshi.Json

data class LocationHeader(
    @Json(name = "ptd")
    val ptd: String?,
    @Json(name = "tpl")
    val tpl: String?,
    @Json(name = "wtd")
    val wtd: String?
)