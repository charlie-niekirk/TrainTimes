package com.cniekirk.traintimes.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crscodes")
data class CRS(
    @PrimaryKey
    val stationName: String,
    val crs: String
)

@Entity(tableName = "recentqueries")
data class RecentQuery(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String
)

@Entity(tableName = "favourites")
data class Favourite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String
)