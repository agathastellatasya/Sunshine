package com.veren.android.sunshine

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat

/**
 * Created by Veren on 6/19/2018.
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private fun setPreferenceSummary(preference: Preference, value: Any) {
        val stringValue = value.toString()
        val key = preference.getKey()

        if (preference is ListPreference) {
            val listPreference = preference as ListPreference
            val prefIndex = listPreference.findIndexOfValue(stringValue)
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex])
            }
        } else {
            preference.setSummary(stringValue)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)
        val sharedPreferences = preferenceScreen.sharedPreferences
        val prefScreen = preferenceScreen
        val count = prefScreen.preferenceCount

        for (i in 0..count - 1) {
            val p = prefScreen.getPreference(i)
            if (p !is CheckBoxPreference) {
                val value = sharedPreferences.getString(p.key, "")
                setPreferenceSummary(p, value)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preference = findPreference(key)
        if (null != preference) {
            if (preference !is CheckBoxPreference) {
                setPreferenceSummary(preference, sharedPreferences!!.getString(key, ""))
            }
        }
    }

}
