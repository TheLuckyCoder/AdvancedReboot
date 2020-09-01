package net.theluckycoder.advancedreboot

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.preference.PreferenceManager
import eu.chainfire.libsuperuser.Shell
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.MANUFACTURER.toString().toLowerCase(Locale.ROOT) == "samsung") {
            findViewById<View>(R.id.bootloaderReboot).visibility = View.GONE
        } else {
            findViewById<View>(R.id.downloadReboot).visibility = View.GONE
        }

        checkForRoot()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 8) {
            val useDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false)

            if (!useDarkTheme && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else if (useDarkTheme && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            recreate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_settings) {
            startActivityForResult(Intent(this, SettingsActivity::class.java), 8)
        }
        return super.onOptionsItemSelected(item)
    }

    fun reboot(view: View) {
        when (view.id) {
            R.id.normalReboot -> confirmDialog(listOf("reboot"))
            R.id.fastReboot -> confirmDialog(listOf("setprop ctl.restart zygote"))
            R.id.recoveryReboot -> confirmDialog(listOf("reboot recovery"))
            R.id.bootloaderReboot -> confirmDialog(listOf("reboot bootloader"))
            R.id.downloadReboot -> confirmDialog(listOf("reboot download"))
            R.id.safeModeReboot -> confirmDialog(listOf("setprop persist.sys.safemode 1", "reboot"))
            R.id.interfaceReboot -> confirmDialog(listOf("pkill com.android.systemui"))
            R.id.shutdownReboot -> confirmDialog(listOf("reboot -p"))
        }
    }

    private fun checkForRoot() {
        if (BuildConfig.DEBUG || Shell.SU.available()) return

        AlertDialog.Builder(this)
            .setTitle(R.string.root_required)
            .setMessage(R.string.root_required_desc)
            .setCancelable(false)
            .setNegativeButton(R.string.quit) { _, _ -> finish() }
            .setPositiveButton(R.string.restart_app) { _, _ -> recreate() }
            .show()
    }

    private fun confirmDialog(commands: List<String>) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("confirm_reboots", true)) {
            AlertDialog.Builder(this)
                .setTitle(R.string.confirm_continue)
                .setMessage(R.string.confirm_continue_desc)
                .setPositiveButton(android.R.string.yes) { _, _ -> showProgressDialog(commands) }
                .setNegativeButton(android.R.string.no, null)
                .show()
        } else {
            showProgressDialog(commands)
        }
    }

    private fun showProgressDialog(commands: List<String>) {
        RebootTask.execute(commands)

        @Suppress("DEPRECATION")
        ProgressDialog(this).apply {
            setTitle(getString(R.string.rebooting_device))
            setCancelable(false)
            isIndeterminate = true
            show()
        }
    }
}
