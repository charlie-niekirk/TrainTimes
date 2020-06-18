package com.cniekirk.traintimes

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import com.cniekirk.traintimes.di.AppInjector
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class TrainTimesApp: Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            it?.let { instanceId ->
                run {
                    Log.e("APP", "Token: ${instanceId.token}")
                }
            }
        }
    }

    override fun androidInjector() = dispatchingAndroidInjector

}