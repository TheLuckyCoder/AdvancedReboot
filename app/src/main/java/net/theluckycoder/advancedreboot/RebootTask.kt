package net.theluckycoder.advancedreboot

import android.os.AsyncTask

import eu.chainfire.libsuperuser.Shell

internal class RebootTask(private val mCommands: Array<String>) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void): Void? {
        Shell.SU.run(mCommands)
        return null
    }
}
