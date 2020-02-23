package com.cniekirk.traintimes.ui.main

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.cniekirk.traintimes.R
import com.google.android.material.transition.MaterialSharedAxis

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val forward = MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.Z, true)
        enterTransition = forward

        val backward = MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.Z, false)
        exitTransition = backward
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

}