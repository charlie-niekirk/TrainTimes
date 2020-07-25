package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt4:nrccMessages")
data class NrccMessages(
    @Element(name = "t5:message") val messages: List<Message>?
)