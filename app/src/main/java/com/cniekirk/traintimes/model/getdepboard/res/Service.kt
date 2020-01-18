package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt7:service")
data class Service(
    @PropertyElement(name = "lt4:std") val scheduledDeparture: String,
    @PropertyElement(name = "lt4:etd") val estimatedDeparture: String?,
    @PropertyElement(name = "lt4:platform") val platform: String?,
    @PropertyElement(name = "lt4:operator") val operator: String,
    @PropertyElement(name = "lt4:operatorCode") val operatorCode: String,
    @PropertyElement(name = "lt4:serviceType") val serviceType: String,
    @PropertyElement(name = "lt4:serviceID") val serviceID: String,
    @Element val origin: Origin,
    @Element val destination: Destination,
    @Element val subsequentCallingPoints: SubsequentCallingPoints
)