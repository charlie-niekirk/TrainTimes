package com.cniekirk.traintimes.di

import android.content.Context
import com.cniekirk.traintimes.TrainTimesApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * Dagger Component class
 */
@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    MainActivityModule::class,
    NetworkRailModule::class,
    CRSModule::class,
    PreferenceModule::class,
    StationModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Context): Builder

        fun build(): AppComponent

    }

    fun inject(trainTimesApp: TrainTimesApp)

}