package com.cniekirk.traintimes.domain.model

import com.cniekirk.traintimes.data.local.model.CRS

data class DepartureAtTimeRequest(
    val from: CRS,
    val to: CRS,
    val time: String
)