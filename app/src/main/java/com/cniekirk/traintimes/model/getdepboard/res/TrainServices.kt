package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt7:trainServices")
data class TrainServices(
    @Element val trainServices: List<Service>
)
