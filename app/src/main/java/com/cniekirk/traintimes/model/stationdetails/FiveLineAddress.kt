package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "add:A_5LineAddress")
data class FiveLineAddress(
    @Element val lines: List<AddressLine>?,
    @PropertyElement(name = "add:PostCode") val postalCode: String?
)