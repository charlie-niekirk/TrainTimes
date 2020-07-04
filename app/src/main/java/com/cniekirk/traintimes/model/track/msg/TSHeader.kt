package com.cniekirk.traintimes.model.track.msg

import com.squareup.moshi.Json

data class TSHeader(
    @Json(name = "rid")
    val rid: String?,
    @Json(name = "ssd")
    val ssd: String?,
    @Json(name = "uid")
    val uid: String?
)