package com.cniekirk.traintimes.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cniekirk.traintimes.data.local.model.CRS

@Dao
interface CRSDao {

    @Query("SELECT * FROM crscodes")
    fun getCrsCodes(): List<CRS>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(crsCodes: List<CRS>)

}