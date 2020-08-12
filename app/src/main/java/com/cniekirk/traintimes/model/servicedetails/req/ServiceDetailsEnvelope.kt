package com.cniekirk.traintimes.model.servicedetails.req

import com.cniekirk.traintimes.model.getdepboard.req.Header
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "v:Envelope")
data class ServiceDetailsEnvelope(
    @Attribute(name = "xmlns:soapenv") val xmlnsSoap: String = "http://www.w3.org/2003/05/soap-envelope",
    @Attribute(name = "xmlns:typ") val xmlnsTyp: String = "http://thalesgroup.com/RTTI/2013-11-28/Token/types",
    @Attribute(name = "xmlns:ldb") val xmlnsLdb: String = "http://thalesgroup.com/RTTI/2016-02-16/ldbsv/",
    @Element val header: Header,
    @Element val serviceDetailsBody: ServiceDetailsBody
)