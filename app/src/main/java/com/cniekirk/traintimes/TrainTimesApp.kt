package com.cniekirk.traintimes

import android.app.Application
import android.util.Log
import com.cniekirk.traintimes.data.prefs.PreferenceProvider
import com.google.firebase.iid.FirebaseInstanceId
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrainTimesApp: Application() {


    override fun onCreate() {
        super.onCreate()
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            it?.let { instanceId ->
                run {
                    Log.e("APP", "Token: ${instanceId.token}")
                    PreferenceProvider(applicationContext).setFirebaseId(instanceId.token)
                }
            }
        }
    }

}