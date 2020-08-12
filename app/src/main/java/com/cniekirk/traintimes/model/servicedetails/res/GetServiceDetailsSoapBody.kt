package com.cniekirk.traintimes.model.servicedetails.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Body")
data class GetServiceDetailsSoapBody(
    @Element(name = "GetServiceDetailsByRIDResponse") val getServiceDetailsByRIDResponse: GetServiceDetailsByRIDResponse
)