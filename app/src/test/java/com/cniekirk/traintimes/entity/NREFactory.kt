package com.cniekirk.traintimes.entity

import com.cniekirk.traintimes.data.local.model.CRS

class NREFactory {

    companion object {

        fun providesCRS(
            crsCode: String = "WAT",
            stationName: String = "London Waterloo"
        ) = listOf(CRS(stationName, crsCode))

    }

}