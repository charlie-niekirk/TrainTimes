package com.cniekirk.traintimes.model.servicedetails.res

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GetServiceDetailsResponse")
data class GetServiceDetailsByRIDResponse(
    @Attribute(name = "xmlns") val xmlns: String,
    @Element(name = "GetServiceDetailsResult") val getServiceDetailsResult: GetServiceDetailsResult
)