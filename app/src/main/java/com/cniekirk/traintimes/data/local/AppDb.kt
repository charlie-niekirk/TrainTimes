package com.cniekirk.traintimes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.data.local.model.RecentQuery

@Database(entities = [CRS::class, RecentQuery::class],
    version = 3,
    exportSchema = false)
abstract class AppDb: RoomDatabase() {

    abstract fun crsDao(): CRSDao
    abstract fun recentQueriesDao(): RecentQueriesDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDb {
            return Room.databaseBuilder(context, AppDb::class.java, "app-db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}