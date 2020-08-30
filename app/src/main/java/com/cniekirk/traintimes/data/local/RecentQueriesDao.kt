package com.cniekirk.traintimes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cniekirk.traintimes.data.local.model.RecentQuery

@Dao
interface RecentQueriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentQuery(recentQuery: RecentQuery)

    @Query("SELECT * FROM recentqueries")
    fun getRecentQueries(): List<RecentQuery>

    @Query("DELETE FROM recentqueries WHERE id NOT IN (SELECT id FROM recentqueries ORDER BY id DESC LIMIT 2)")
    fun cleanQueries()

}