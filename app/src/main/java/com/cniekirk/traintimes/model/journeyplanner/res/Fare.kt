package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class Fare(
    @Json(name = "adultStatusCode")
    val adultStatusCode: String?,
    @Json(name = "canCrossLondon")
    val canCrossLondon: String?,
    @Json(name = "childStatusCode")
    val childStatusCode: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "destinationNlc")
    val destinationNlc: String?,
    @Json(name = "direction")
    val direction: String?,
    @Json(name = "endLegId")
    val endLegId: String?,
    @Json(name = "fareCategory")
    val fareCategory: String?,
    @Json(name = "fareClass")
    val fareClass: String?,
    @Json(name = "fareSetter")
    val fareSetter: String?,
    @Json(name = "fareType")
    val fareType: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "individualFare")
    val individualFare: IndividualFare?,
    @Json(name = "originNlc")
    val originNlc: String?,
    @Json(name = "routeCode")
    val routeCode: String?,
    @Json(name = "startLegId")
    val startLegId: String?,
    @Json(name = "ticketRestriction")
    val ticketRestriction: String?,
    @Json(name = "totalPrice")
    val totalPrice: String?,
    @Json(name = "typeCode")
    val typeCode: String?,
    @Json(name = "undiscountedPrices")
    val undiscountedPrices: UndiscountedPrices?,
    @Json(name = "utsZones")
    val utsZones: String?
)