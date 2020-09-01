package net.theluckycoder.advancedreboot

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

@Suppress("unused")
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}
