package net.theluckycoder.advancedreboot

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.google.android.gms.ads.MobileAds


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        MobileAds.initialize(this, "ca-app-pub-1279472163660969~1358566332")
    }

}

