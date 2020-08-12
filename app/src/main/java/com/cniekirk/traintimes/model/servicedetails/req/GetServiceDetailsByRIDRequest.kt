package com.cniekirk.traintimes.model.servicedetails.req

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "ldb:GetServiceDetailsByRIDRequest")
data class GetServiceDetailsByRIDRequest(
    @Element val rid: Rid
)