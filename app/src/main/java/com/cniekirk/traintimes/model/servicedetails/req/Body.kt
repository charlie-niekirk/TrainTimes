package com.cniekirk.traintimes.model.servicedetails.req

import com.cniekirk.traintimes.model.getdepboard.req.GetDepBoardWithDetailsRequest
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "v:Body")
data class Body(
    @Element val getServiceDetailsRequest: GetServiceDetailsRequest
)