package net.theluckycoder.advancedreboot;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import eu.chainfire.libsuperuser.Shell;

public final class MainActivity extends AppCompatActivity {

    static { AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); }

    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        if (Build.MANUFACTURER.matches("samsung"))
            findViewById(R.id.bootloaderReboot).setVisibility(View.GONE);
        else
            findViewById(R.id.downloadReboot).setVisibility(View.GONE);

        checkForRoot();

        //Init AdMob
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("304C7D4CF3DD1D2C556771826CCF9037")
                .build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1279472163660969/3259304920");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void reboot(View view) {
        int id = view.getId();

        Bundle params = new Bundle();

        switch (id) {
            case R.id.normalReboot:
                confirmDialog(new String[]{"reboot"});
                params.putString("type", "normal");
                break;
            case R.id.fastReboot:
                confirmDialog(new String[]{"setprop ctl.restart zygote"});
                params.putString("type", "fast");
                break;
            case R.id.recoveryReboot:
                confirmDialog(new String[]{"reboot recovery"});
                params.putString("type", "recovery");
                break;
            case R.id.bootloaderReboot:
                confirmDialog(new String[]{"reboot bootloader"});
                params.putString("type", "bootloader");
                break;
            case R.id.downloadReboot:
                confirmDialog(new String[]{"reboot download"});
                params.putString("type", "bootloader");
                break;
            case R.id.safeModeReboot:
                confirmDialog(new String[]{"setprop persist.sys.safemode 1", "reboot"});
                params.putString("type", "safeMode");
                break;
            case R.id.interfaceReboot:
                confirmDialog(new String[]{"pkill com.android.systemui"});
                params.putString("type", "interface");
                break;
            case R.id.shutdownReboot:
                confirmDialog(new String[]{"reboot -p"});
                params.putString("type", "shutdown");
                break;
        }

        mFirebaseAnalytics.logEvent("reboot", params);
    }

    private void checkForRoot() {
        if (!Shell.SU.available()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.root_required);
            dialog.setMessage(R.string.root_required_desc);
            dialog.setCancelable(false);
            dialog.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            dialog.setPositiveButton(R.string.restart_app, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    recreate();
                }
            });
            dialog.show();
        }
    }

    private void confirmDialog(final String[] commands) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("confirm_reboots", true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.confirm_continue);
            builder.setMessage(R.string.confirm_continue_desc);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showProgressDialog(commands);
                }
            });
            builder.setNegativeButton(android.R.string.no, null);
            builder.show();

            if(mInterstitialAd.isLoaded())
                mInterstitialAd.show();
        } else
            showProgressDialog(commands);
    }

    private void showProgressDialog(String[] commands) {
        new RebootTask(commands).execute();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.rebooting_device));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("304C7D4CF3DD1D2C556771826CCF9037")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

}
