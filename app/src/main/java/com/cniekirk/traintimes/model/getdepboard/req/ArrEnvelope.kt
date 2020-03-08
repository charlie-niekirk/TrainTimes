package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "v:Envelope")
data class ArrEnvelope(
    @Attribute(name = "xmlns:i") val xmlnsI: String = "http://www.w3.org/2001/XMLSchema-instance",
    @Attribute(name = "xmlns:d") val xmlnsD: String = "http://www.w3.org/2001/XMLSchema",
    @Attribute(name = "xmlns:c") val xmlnsC: String = "http://www.w3.org/2003/05/soap-encoding",
    @Attribute(name = "xmlns:v") val xmlnsV: String = "http://www.w3.org/2003/05/soap-envelope",
    @Element val header: Header,
    @Element val body: ArrBody
)