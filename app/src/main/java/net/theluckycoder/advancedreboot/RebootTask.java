package net.theluckycoder.advancedreboot;

import android.os.AsyncTask;

import eu.chainfire.libsuperuser.Shell;

final class RebootTask extends AsyncTask<Void, Void, Void> {

    private final String[] mCommands;

    RebootTask(String[] commands) {
        mCommands = commands;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Shell.SU.run(mCommands);
        return null;
    }
}
