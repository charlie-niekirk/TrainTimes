package com.cniekirk.traintimes.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crscodes")
data class CRS(
    @PrimaryKey
    val crs: String,
    val stationName:String
)