package com.cniekirk.traintimes.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.cniekirk.traintimes.TrainTimesApp
import dagger.android.AndroidInjection
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection

/**
 * Injector to automatically inject lifecycle aware components like Fragments and Activities
 */
object AppInjector {

    fun init(app : TrainTimesApp) {

        DaggerAppComponent.builder().application(app).build().inject(app)

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, p1: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }
        })

    }

    private fun handleActivity(activity: Activity) {
        if (activity is HasAndroidInjector) {
            AndroidInjection.inject(activity)
        }
        if(activity is FragmentActivity) {

            activity
                .supportFragmentManager
                .registerFragmentLifecycleCallbacks(
                    object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                            if(f is Injectable) {
                                AndroidSupportInjection.inject(f)
                            }
                        }
                    } , true
                )

        }

    }


}