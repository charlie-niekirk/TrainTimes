package com.cniekirk.traintimes.model.servicedetails.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GetServiceDetailsRequest")
data class GetServiceDetailsRequest(
    @Attribute(name = "xmlns") val xmlns: String = "http://thalesgroup.com/RTTI/2017-10-01/ldb/",
    @Attribute(name = "id") val id: String = "o0",
    @Attribute(name = "c:root") val cRoot: String = "1",
    @Element val serviceID: ServiceId
)