package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "StationFacilities")
data class StationFacilities(
    @Element(name = "SeatedArea") val seatedArea: SeatedArea?,
    @Element(name = "StationBuffet") val stationBuffet: StationBuffet?,
    @Element(name = "Toilets") val toilets: Toilets?,
    @Element(name = "BabyChange") val babyChange: BabyChange?,
    @Element(name = "Showers") val showers: Showers?,
    @Element(name = "Telephones") val telephones: Telephones?,
    @Element(name = "WiFi") val wiFi: WiFi?,
    @Element(name = "PostBox") val postBox: PostBox?,
    @Element(name = "AtmMachine") val atmMachine: AtmMachine?,
    @Element(name = "BureauDeChange") val bureauDeChange: BureauDeChange?,
    @Element(name = "Shops") val shops: Shops?
)