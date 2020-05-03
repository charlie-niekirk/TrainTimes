package com.cniekirk.traintimes.di

import android.content.Context
import com.cniekirk.traintimes.data.prefs.PreferenceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferenceModule {

    @Provides
    @Singleton
    fun providePrefs(context: Context): PreferenceProvider {
        return PreferenceProvider(context)
    }

}