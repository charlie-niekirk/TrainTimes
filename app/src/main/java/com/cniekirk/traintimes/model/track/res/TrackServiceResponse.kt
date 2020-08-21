package com.cniekirk.traintimes.model.track.res

import com.squareup.moshi.Json

data class TrackServiceResponse(
    @Json(name = "tracking") val tracking: Boolean
)