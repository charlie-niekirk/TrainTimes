package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t12:trainServices")
data class TrainServices(
    @Element val trainServices: List<Service>
)
