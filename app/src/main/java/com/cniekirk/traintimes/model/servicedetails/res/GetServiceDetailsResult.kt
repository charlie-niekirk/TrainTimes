package com.cniekirk.traintimes.model.servicedetails.res

import com.cniekirk.traintimes.model.getdepboard.res.PreviousCallingPoints
import com.cniekirk.traintimes.model.getdepboard.res.SubsequentCallingPoints
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GetServiceDetailsResult")
data class GetServiceDetailsResult(
    @Attribute(name = "xmlns:lt") val lt: String,
    @Attribute(name = "xmlns:lt2") val lt2: String,
    @Attribute(name = "xmlns:lt3") val lt3: String,
    @Attribute(name = "xmlns:lt4") val lt4: String,
    @Attribute(name = "xmlns:lt5") val lt5: String,
    @Attribute(name = "xmlns:lt6") val lt6: String,
    @Attribute(name = "xmlns:lt7") val lt7: String,

    @PropertyElement(name = "lt7:generatedAt") val generatedAt: String?,
    @PropertyElement(name = "lt7:locationName") val locationName: String?,
    @PropertyElement(name = "lt7:crs") val stationCode: String?,
    @PropertyElement(name = "lt7:serviceType") val serviceType: String?,
    @PropertyElement(name = "lt7:operator") val operator: String?,
    @PropertyElement(name = "lt7:operatorCode") val operatorCode: String?,
    @PropertyElement(name = "lt7:length") val length: String?,
    @PropertyElement(name = "lt7:sta") val sta: String?,
    @PropertyElement(name = "lt7:eta") val eta: String?,
    @PropertyElement(name = "lt7:ata") val ata: String?,
    @PropertyElement(name = "lt7:std") val std: String?,
    @PropertyElement(name = "lt7:etd") val etd: String?,
    @PropertyElement(name = "lt7:atd") val atd: String?,
    @PropertyElement(name = "lt7:platform") val platform: String?,
    @PropertyElement(name = "lt7:cancelReason") val cancelReason: String?,
    @PropertyElement(name = "lt7:delayReason") val delayReason: String?,
    @PropertyElement(name = "lt7:overdueMessage") val overdueMessage: String?,
    @PropertyElement(name = "lt7:detachFront") val detachFront: String?,
    @PropertyElement(name = "lt7:isReverseFormation") val isReverseFormation: Boolean?,
    @Element(name = "lt7:previousCallingPoints") val previousCallingPoints: PreviousCallingPoints?,
    @Element(name = "lt7:subsequentCallingPoints") val subsequentCallingPoints: SubsequentCallingPoints?
)