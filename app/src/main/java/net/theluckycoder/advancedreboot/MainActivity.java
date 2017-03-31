package net.theluckycoder.advancedreboot;

import android.app.ProgressDialog;
import android.content.Context;
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

import static net.theluckycoder.advancedreboot.R.id.adView;

public final class MainActivity extends AppCompatActivity {

    static { AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); }
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        context = this;

        if (Build.MANUFACTURER.matches("samsung"))
            findViewById(R.id.bootloaderReboot).setVisibility(View.GONE);
        else
            findViewById(R.id.downloadReboot).setVisibility(View.GONE);

        final AdView mAdView = (AdView) findViewById(adView);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template")
                .build();
        mAdView.loadAd(adRequest);
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
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void reboot(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.normalReboot:
                confirmDialog(new String[]{"reboot"});
                break;
            case R.id.fastReboot:
                confirmDialog(new String[]{"setprop ctl.restart zygote"});
                break;
            case R.id.recoveryReboot:
                confirmDialog(new String[]{"reboot recovery"});
                break;
            case R.id.bootloaderReboot:
                confirmDialog(new String[]{"reboot bootloader"});
                break;
            case R.id.downloadReboot:
                confirmDialog(new String[]{"reboot download"});
                break;
            case R.id.safeModeReboot:
                confirmDialog(new String[]{"setprop persist.sys.safemode 1", "reboot"});
                break;
            case R.id.shutdownReboot:
                confirmDialog(new String[]{"reboot -p"});
                break;
        }
    }

    private void confirmDialog(final String[] commands) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("confirm_reboots", true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Do you want to continue");
            builder.setMessage("Are you sure you want to reboot your phone?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    showProgressDialog(commands);
                }
            });
            builder.setNegativeButton(android.R.string.no, null);
            builder.show();
        } else
            showProgressDialog(commands);
    }

    private void showProgressDialog(String[] commands) {
        new RebootTask(commands).execute();
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(getString(R.string.rebooting_device));
        progressDialog.show();
    }
}
