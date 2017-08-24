package net.theluckycoder.advancedreboot

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.analytics.FirebaseAnalytics
import eu.chainfire.libsuperuser.Shell

class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.app_name)

        if (Build.MANUFACTURER.toString().toLowerCase() == ("samsung"))
            findViewById<View>(R.id.bootloaderReboot).visibility = View.GONE
        else
            findViewById<View>(R.id.downloadReboot).visibility = View.GONE

        checkForRoot()

        //Init AdMob
        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder()
                .addTestDevice("304C7D4CF3DD1D2C556771826CCF9037")
                .build()
        mAdView.loadAd(adRequest)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-1279472163660969/3259304920"
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                requestNewInterstitial()
            }
        }
        requestNewInterstitial()

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_settings)
                startActivity(Intent(this, SettingsActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    fun reboot(view: View) {
        val params = Bundle()

        when (view.id) {
            R.id.normalReboot -> {
                confirmDialog(arrayOf("reboot"))
                params.putString("type", "normal")
            }
            R.id.fastReboot -> {
                confirmDialog(arrayOf("setprop ctl.restart zygote"))
                params.putString("type", "fast")
            }
            R.id.recoveryReboot -> {
                confirmDialog(arrayOf("reboot recovery"))
                params.putString("type", "recovery")
            }
            R.id.bootloaderReboot -> {
                confirmDialog(arrayOf("reboot bootloader"))
                params.putString("type", "bootloader")
            }
            R.id.downloadReboot -> {
                confirmDialog(arrayOf("reboot download"))
                params.putString("type", "bootloader")
            }
            R.id.safeModeReboot -> {
                confirmDialog(arrayOf("setprop persist.sys.safemode 1", "reboot"))
                params.putString("type", "safeMode")
            }
            R.id.interfaceReboot -> {
                confirmDialog(arrayOf("pkill com.android.systemui"))
                params.putString("type", "interface")
            }
            R.id.shutdownReboot -> {
                confirmDialog(arrayOf("reboot -p"))
                params.putString("type", "shutdown")
            }
        }

        mFirebaseAnalytics.logEvent("reboot", params)
    }

    private fun checkForRoot() {
        if (!Shell.SU.available()) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle(R.string.root_required)
            dialog.setMessage(R.string.root_required_desc)
            dialog.setCancelable(false)
            dialog.setNegativeButton(R.string.quit) { _, _ -> finish() }
            dialog.setPositiveButton(R.string.restart_app) { _, _ -> recreate() }
            dialog.show()
        }
    }

    private fun confirmDialog(commands: Array<String>) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("confirm_reboots", true)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.confirm_continue)
            builder.setMessage(R.string.confirm_continue_desc)
            builder.setPositiveButton(android.R.string.yes) { _, _ -> showProgressDialog(commands) }
            builder.setNegativeButton(android.R.string.no, null)
            builder.show()

            if (mInterstitialAd.isLoaded) mInterstitialAd.show()
        } else
            showProgressDialog(commands)
    }

    private fun showProgressDialog(commands: Array<String>) {
        RebootTask(commands).execute()
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.rebooting_device))
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    private fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder()
                .addTestDevice("304C7D4CF3DD1D2C556771826CCF9037")
                .build()

        mInterstitialAd.loadAd(adRequest)
    }

    companion object {
        init { AppCompatDelegate.setCompatVectorFromResourcesEnabled(true) }
    }

}
