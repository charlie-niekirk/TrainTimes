package com.cniekirk.traintimes.model.track.msg

import com.squareup.moshi.Json

data class Location(
    @Json(name = "dep")
    val dep: List<Dep>?,
    @Json(name = "header")
    val locationHeader: LocationHeader?,
    @Json(name = "plat")
    val plat: List<String>?
)