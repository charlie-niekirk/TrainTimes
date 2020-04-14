package com.cniekirk.traintimes

import android.app.Application
import android.content.res.Configuration
import com.cniekirk.traintimes.di.AppInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class TrainTimesApp: Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

    override fun androidInjector() = dispatchingAndroidInjector

}