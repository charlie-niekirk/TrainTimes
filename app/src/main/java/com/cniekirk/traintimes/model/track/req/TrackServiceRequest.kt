package com.cniekirk.traintimes.model.track.req

import com.squareup.moshi.Json

data class TrackServiceRequest(
    @Json(name = "rid") val rid: String,
    @Json(name = "tploc") val tploc: String,
    @Json(name = "fbid") val fbid: String
)