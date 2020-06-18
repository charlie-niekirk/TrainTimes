package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "Address")
data class Address(
    @Element(name = "PostalAddress") val postalAddress: PostalAddress?
)