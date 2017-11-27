package net.theluckycoder.advancedreboot

import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate



class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        findPreference("dark_theme").setOnPreferenceClickListener {
            if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("dark_theme", false)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            val intent = activity.intent
            activity.finish()
            startActivity(intent)
            true
        }
    }
}
