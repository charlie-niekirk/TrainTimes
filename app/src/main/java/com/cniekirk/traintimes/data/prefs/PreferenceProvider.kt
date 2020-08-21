package com.cniekirk.traintimes.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

private const val SHOW_PRICES = "show_prices"
private const val DEFAULT_PAGE = "default_page_live"
private const val FIREBASE_ID = "fb_id"

class PreferenceProvider(context: Context) {

    private val appContext = context.applicationContext

    private val preferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun getShouldShowPrices(): Boolean {
        return preferences.getBoolean(SHOW_PRICES, false)
    }

    fun getIsLiveDefaultPage(): Boolean {
        return preferences.getBoolean(DEFAULT_PAGE, false)
    }

    fun setFirebaseId(fbId: String) {
        preferences.edit().putString(FIREBASE_ID, fbId).apply()
    }

    fun getFirebaseId(): String {
        return preferences.getString(FIREBASE_ID, "")!!
    }

}