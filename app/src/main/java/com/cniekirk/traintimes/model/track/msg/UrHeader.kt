package com.cniekirk.traintimes.model.track.msg

import com.squareup.moshi.Json

data class UrHeader(
    @Json(name = "requestID")
    val requestID: String?,
    @Json(name = "requestSource")
    val requestSource: String?,
    @Json(name = "updateOrigin")
    val updateOrigin: String?
)