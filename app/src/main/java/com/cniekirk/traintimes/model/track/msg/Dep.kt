package com.cniekirk.traintimes.model.track.msg

import com.squareup.moshi.Json

data class Dep(
    @Json(name = "header")
    val depHeader: DepHeader?
)