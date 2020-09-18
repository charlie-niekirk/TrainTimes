package com.cniekirk.traintimes.di

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import com.cniekirk.traintimes.data.prefs.PreferenceProvider
import com.cniekirk.traintimes.data.proto.FavouritesSerializer
import com.cniekirk.traintimes.model.Favourites
import com.cniekirk.traintimes.utils.ConnectionStateEmitter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CommonModule {

    @Provides
    @Singleton
    fun providePrefs(context: Context): PreferenceProvider {
        return PreferenceProvider(context)
    }

    @Provides
    @Singleton
    fun provideFavouritesDataStore(context: Context): DataStore<Favourites> {
        return context.createDataStore(
            "favourite_routes.pb",
            serializer = FavouritesSerializer
        )
    }

    @Provides
    @Singleton
    fun provideConnectionStateEmitter(context: Context): ConnectionStateEmitter {
        return ConnectionStateEmitter(context)
    }

}