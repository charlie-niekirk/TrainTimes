package com.cniekirk.traintimes

import android.app.Application
import android.util.Log
import com.cniekirk.traintimes.data.prefs.PreferenceProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TrainTimesApp: Application() {


    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ProductionTree())
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            it?.let { instanceId ->
                Timber.d("Token: $instanceId")
                PreferenceProvider(applicationContext).setFirebaseId(instanceId)
            }
        }
    }

    class ProductionTree: Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {

            if (priority == Log.VERBOSE || priority == Log.DEBUG)
                return

            FirebaseCrashlytics.getInstance().log(message)

            throwable?.let {
                when(priority) {
                    Log.ERROR, Log.WARN -> FirebaseCrashlytics.getInstance().recordException(it)
                }
            }

        }
    }

}