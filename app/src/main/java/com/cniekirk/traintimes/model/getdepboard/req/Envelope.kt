package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Envelope")
data class Envelope(
    @Attribute(name = "xmlns:soap") val xmlnsSoapenv: String = "http://schemas.xmlsoap.org/soap/envelope/",
    @Attribute(name = "xmlns:typ") val xmlnsI: String = "http://thalesgroup.com/RTTI/2013-11-28/Token/types",
    @Attribute(name = "xmlns:ldb") val xmlnsD: String = "http://thalesgroup.com/RTTI/2017-10-01/ldbsv/",
//    @Attribute(name = "xmlns:c") val xmlnsC: String = "http://www.w3.org/2003/05/soap-encoding",
//    @Attribute(name = "xmlns:v") val xmlnsV: String = "http://www.w3.org/2003/05/soap-envelope",
    @Element val header: Header,
    @Element val body: Body
)