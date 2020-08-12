package com.cniekirk.traintimes.model.getdepboard.res

import com.fasterxml.jackson.databind.annotation.JsonAppend
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t6:location")
data class StartEndLocation(
    @PropertyElement(name = "t5:locationName") val locationName: String,
    @PropertyElement(name = "t5:crs") val crs: String,
    @PropertyElement(name = "t5:tiploc") val tiploc: String
)