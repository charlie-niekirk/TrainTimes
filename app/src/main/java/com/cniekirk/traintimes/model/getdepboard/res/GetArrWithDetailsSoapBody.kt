package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Body")
data class GetArrWithDetailsSoapBody(
    @Element(name = "GetArrBoardWithDetailsResponse") val getArrBoardWithDetailsResponse:
    GetArrBoardWithDetailsResponse
)