package com.cniekirk.traintimes.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.cniekirk.traintimes.R
import com.google.android.material.transition.MaterialSharedAxis

class SettingsFragment: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        exitTransition = backward

        val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        enterTransition = forward
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key.equals(getString(R.string.key_dark_mode), true)) {
            if (sharedPreferences.getBoolean(getString(R.string.key_dark_mode), false)) {

            } else {

            }
        }
    }

}