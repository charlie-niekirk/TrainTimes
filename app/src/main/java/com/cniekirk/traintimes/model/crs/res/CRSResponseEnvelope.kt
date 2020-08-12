package com.cniekirk.traintimes.model.crs.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Envelope")
data class CRSResponseEnvelope(
    @Element val crsResponseBody: CRSResponseBody
)