package com.cniekirk.traintimes.model.servicedetails.res

import com.cniekirk.traintimes.model.getdepboard.res.Location
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GetServiceDetailsResult")
data class GetServiceDetailsResult(
//    @Attribute(name = "xmlns:t5") val lt: String,
//    @Attribute(name = "xmlns:c3") val lt2: String,
//    @Attribute(name = "xmlns:c4") val lt3: String,
//    @Attribute(name = "xmlns:t10") val lt4: String,
//    @Attribute(name = "xmlns:t9") val lt5: String,
//    @Attribute(name = "xmlns:t6") val lt6: String,
//    @Attribute(name = "xmlns:c1") val lt7: String,

    @PropertyElement(name = "t10:generatedAt") val generatedAt: String?,
    @PropertyElement(name = "t10:rid") val rid: String?,
    @PropertyElement(name = "t10:uid") val uid: String?,
    @PropertyElement(name = "t10:trainid") val trainId: String?,
    @PropertyElement(name = "t10:operator") val operator: String?,
    @PropertyElement(name = "t10:operatorCode") val operatorCode: String?,
    @PropertyElement(name = "t10:sdd") val sdd: String?,
    @PropertyElement(name = "t10:serviceType") val serviceType: String?,
    @PropertyElement(name = "t10:delayReason") val delayReason: Int?,
    @PropertyElement(name = "t10:category") val category: String?,
    @Element val locations: Locations?
)