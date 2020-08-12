package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t10:location")
data class Location(
    @Attribute(name = "xmlns:t7") val xmlnsT7: String?,
    @PropertyElement(name = "t9:isOperational") val isOperational: Boolean?,
    @PropertyElement(name = "t9:isCancelled") val isCancelled: Boolean?,
    @PropertyElement(name = "t9:isPass") val isPass: Boolean?,
    @PropertyElement(name = "t9:platform") val platform: String?,
    @PropertyElement(name = "t9:platformIsHidden") val platformIsHidden: Boolean?,
    @PropertyElement(name = "t9:serviceIsSupressed") val serviceIsSupressed: Boolean?,
    @PropertyElement(name = "t9:sta") val sta: String?,
    @PropertyElement(name = "t9:eta") val eta: String?,
    @PropertyElement(name = "t9:ata") val ata: String?,
    @PropertyElement(name = "t9:arrivalType") val arrivalType: String?,
    @PropertyElement(name = "t9:arrivalSource") val arrivalSource: String?,
    @PropertyElement(name = "t9:std") val std: String?,
    @PropertyElement(name = "t9:etd") val etd: String?,
    @PropertyElement(name = "t9:atd") val atd: String?,
    @PropertyElement(name = "t9:departureType") val departureType: String?,
    @PropertyElement(name = "t9:departureSource") val departureSource: String?,
    @PropertyElement(name = "t9:lateness") val lateness: String?,
    @PropertyElement(name = "t10:locationName") val locationName: String?,
    @PropertyElement(name = "t10:tiploc") val tiploc: String?,
    @PropertyElement(name = "t10:crs") val stationCode: String?,
    @Element(name = "t10:associations") val associations: List<Association>?,
    @PropertyElement(name = "t10:activities") val activities: String?,
    @PropertyElement(name = "t10:length") val length: Int?
)