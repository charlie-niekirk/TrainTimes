package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "PostalAddress")
data class PostalAddress(
    @Element(name = "add:A_5LineAddress") val fiveLineAddress: FiveLineAddress?
)