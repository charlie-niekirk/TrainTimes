package com.cniekirk.traintimes.data.local

import androidx.room.*
import com.cniekirk.traintimes.data.local.model.Favourite

@Dao
interface FavouritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavourite(favourite: Favourite)

    @Query("SELECT * FROM favourites")
    fun getFavourites(): List<Favourite>

    @Delete
    fun deleteFavourite(favourite: Favourite)

}