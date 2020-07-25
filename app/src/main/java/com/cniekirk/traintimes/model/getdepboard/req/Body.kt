package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soapenv:Body")
data class Body(
    @Element val getDepBoardWithDetailsRequest: GetDepBoardWithDetailsRequest
)