package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GetArrBoardWithDetailsResponse")
data class GetArrBoardWithDetailsResponse(
    @Attribute(name = "xmlns") val xmlns: String,
    @Element(name = "GetStationBoardResult") val getStationBoardResult: GetStationBoardResult
)