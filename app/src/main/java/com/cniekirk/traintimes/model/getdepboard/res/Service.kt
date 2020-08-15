package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t12:service")
data class Service(
    @PropertyElement(name = "t10:std") val scheduledDeparture: String?,
    @PropertyElement(name = "t10:etd") val estimatedDeparture: String?,
    @PropertyElement(name = "t10:platform") val platform: String?,
    @PropertyElement(name = "t10:platformIsHidden") val platformIsHidden: Boolean?,
    @PropertyElement(name = "t10:operator") val operator: String,
    @PropertyElement(name = "t10:operatorCode") val operatorCode: String,
    @PropertyElement(name = "t10:serviceType") val serviceType: String?,
    @PropertyElement(name = "t10:rid") val rid: String,
    @PropertyElement(name = "t10:length") val length: String?,
    @Element val origin: Origin,
    @Element val destination: Destination,
    @Element val subsequentLocations: SubsequentLocations?//,
//    @Element val previousCallingPoints: PreviousCallingPoints?
)