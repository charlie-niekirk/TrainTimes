package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t10:association")
data class Association(
    @PropertyElement(name = "t10:category") val category: String?,
    @PropertyElement(name = "t10:rid") val rid: String?,
    @PropertyElement(name = "t10:uid") val uid: String?,
    @PropertyElement(name = "t10:trainid") val trainid: String?,
    @PropertyElement(name = "t10:rsid") val rsid: String?,
    @PropertyElement(name = "t10:sdd") val sdd: String?,
    @PropertyElement(name = "t10:origin") val origin: String?,
    @PropertyElement(name = "t10:originCRS") val originCRS: String?,
    @PropertyElement(name = "t10:originTiploc") val originTiploc: String?,
    @PropertyElement(name = "t10:destination") val destination: String?,
    @PropertyElement(name = "t10:destinationCRS") val destinationCRS: String?,
    @PropertyElement(name = "t10:destinationTiploc") val destinationTiploc: String?
)
