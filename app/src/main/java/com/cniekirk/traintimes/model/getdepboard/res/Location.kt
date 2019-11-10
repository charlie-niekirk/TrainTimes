package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt4:location")
data class Location(
    @PropertyElement(name = "lt4:locationName") val locationName: String,
    @PropertyElement(name = "lt4:crs") val stationCode: String
)