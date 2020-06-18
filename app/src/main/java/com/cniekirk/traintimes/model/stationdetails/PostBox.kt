package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "PostBox")
data class PostBox(
    @PropertyElement(name = "com:Note") val note: String?,
    @PropertyElement(name = "com:Available") val available: Boolean?
)