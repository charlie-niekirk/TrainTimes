package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GetDepBoardWithDetailsResponse")
data class GetDepBoardWithDetailsResponse(
    @Attribute(name = "xmlns") val xmlns: String,
    @Element val getBoardWithDetailsResult: GetBoardWithDetailsResult
)