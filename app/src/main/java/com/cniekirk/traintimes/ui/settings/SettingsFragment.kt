package com.cniekirk.traintimes.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.cniekirk.traintimes.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_preferences)
    }

}