package net.theluckycoder.advancedreboot

import eu.chainfire.libsuperuser.Shell
import java.util.concurrent.Executors

internal object RebootTask {

    private val executor = Executors.newSingleThreadExecutor()

    fun execute(commands: List<String>) {
        executor.execute {
            Shell.SU.run(commands)
        }
    }
}
