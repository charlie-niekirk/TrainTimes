package com.cniekirk.traintimes.di

import android.content.Context
import com.cniekirk.traintimes.data.prefs.PreferenceProvider
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
    fun provideConnectionStateEmitter(context: Context): ConnectionStateEmitter {
        return ConnectionStateEmitter(context)
    }

}