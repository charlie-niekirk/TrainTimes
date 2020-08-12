package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t6:nrccMessages")
data class NrccMessages(
    @Element(name = "t5:message") val messages: List<Message>?
)