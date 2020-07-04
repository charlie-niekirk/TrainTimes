package com.cniekirk.traintimes.model.track.req

import com.squareup.moshi.Json

data class TrackServiceRequest(
    @Json(name = "rid") val retailId: String,
    @Json(name = "tploc") val tipLoc: String,
    @Json(name = "fbid") val firebaseId: String
)