package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "ldb:GetDepBoardWithDetailsRequest")
data class GetDepBoardWithDetailsRequest(
//    @Attribute(name = "xmlns") val xmlns: String = "http://thalesgroup.com/RTTI/2017-10-01/ldb/",
//    @Attribute(name = "id") val id: String = "o0",
//    @Attribute(name = "c:root") val cRoot: String = "1",
    @PropertyElement(name = "ldb:numRows") val numRows: NumRows,
    @PropertyElement(name = "ldb:crs") val crs: String,
    @PropertyElement(name = "ldb:time") val time: String,
    @PropertyElement(name = "ldb:timeWindow") val timeWindow: Int,
    @PropertyElement(name = "ldb:filterCrs") val filterCrs: String,
    @PropertyElement(name = "ldb:filterType") val filterType: String,
    @PropertyElement(name = "ldb:filterTOC") val filterToc: String,
    @PropertyElement(name = "ldb:services") val services: String,
    @PropertyElement(name = "ldb:getNonPassengerServices") val getNonPassengerServices: Boolean
)