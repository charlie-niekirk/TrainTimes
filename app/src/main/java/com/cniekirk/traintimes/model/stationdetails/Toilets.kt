package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "Toilets")
data class Toilets(
    @Element(name = "com:Annotation") val annotation: Annotation?,
    @PropertyElement(name = "Available") val available: Boolean?
)