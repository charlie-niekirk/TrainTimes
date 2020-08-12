package com.cniekirk.traintimes.model.crs.req

import com.cniekirk.traintimes.model.crs.req.CRSRequestBody
import com.cniekirk.traintimes.model.getdepboard.req.Header
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Envelope")
data class CRSRequestEnvelope(
    @Attribute(name = "xmlns:soap") val soap: String = "http://www.w3.org/2003/05/soap-envelope",
    @Attribute(name = "xmlns:typ") val typ: String = "http://thalesgroup.com/RTTI/2013-11-28/Token/types",
    @Attribute(name = "xmlns:ldb") val ldb: String = "http://thalesgroup.com/RTTI/2015-05-14/ldbsv_ref/",
    @Element val header: Header,
    @Element val crsRequestBody: CRSRequestBody
)