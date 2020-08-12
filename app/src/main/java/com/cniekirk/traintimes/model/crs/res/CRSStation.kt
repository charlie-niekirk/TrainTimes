package com.cniekirk.traintimes.model.crs.res

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "Station")
data class CRSStation(
    @Attribute(name = "crs") val crs: String,
    @TextContent val stationName: String
)
