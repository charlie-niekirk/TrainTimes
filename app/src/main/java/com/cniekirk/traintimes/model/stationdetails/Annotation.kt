package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "com:Annotation")
data class Annotation(
    @PropertyElement(name = "com:Note") val note: String?
)