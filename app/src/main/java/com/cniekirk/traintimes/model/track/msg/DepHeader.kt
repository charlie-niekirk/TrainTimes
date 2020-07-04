package com.cniekirk.traintimes.model.track.msg

import com.squareup.moshi.Json

data class DepHeader(
    @Json(name = "et")
    val et: String?,
    @Json(name = "src")
    val src: String?
)