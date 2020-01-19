package com.cniekirk.traintimes.model.servicedetails.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Envelope")
data class GetServiceDetailsSoapEnvelope(
    @Element(name = "soap:Body") val body: GetServiceDetailsSoapBody
)