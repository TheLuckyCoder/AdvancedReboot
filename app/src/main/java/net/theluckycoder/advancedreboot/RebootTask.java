package net.theluckycoder.advancedreboot;

import android.os.AsyncTask;

import eu.chainfire.libsuperuser.Shell;

final class RebootTask extends AsyncTask<Void, Void, Void> {

    private final String[] commands;

    RebootTask(String[] commands) {
        this.commands = commands;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Shell.SU.available())
            Shell.SU.run(commands);
        return null;
    }
}
