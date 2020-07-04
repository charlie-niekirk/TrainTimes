package com.cniekirk.traintimes.model.track.msg

import com.squareup.moshi.Json

data class TrainUpdateMessageItem(
    @Json(name = "header")
    val urHeader: UrHeader?,
    @Json(name = "TS")
    val tS: List<TS>?
)