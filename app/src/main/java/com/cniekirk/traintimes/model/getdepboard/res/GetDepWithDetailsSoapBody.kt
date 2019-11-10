package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Body")
data class GetDepWithDetailsSoapBody(
    @Element(name = "GetDepBoardWithDetailsResponse") val getDepBoardWithDetailsResponse:
    GetDepBoardWithDetailsResponse
)