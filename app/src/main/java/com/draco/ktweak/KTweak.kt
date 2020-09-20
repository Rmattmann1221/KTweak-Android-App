package com.draco.ktweak

import android.content.Context
import java.io.File

class KTweak(private val context: Context) {
    companion object {
        const val scriptName = "ktweak"
        const val logName = "log"
    }


    fun execute(callback: ((Int) -> Unit)? = null) {
        val scriptBytes = context.assets.open(scriptName).readBytes()

        val script = File(context.filesDir, scriptName)
        script.writeBytes(scriptBytes)

        val log = File(context.filesDir, logName)

        val process = ProcessBuilder("su", "-c", "sh", script.absolutePath)
            .redirectErrorStream(true)
            .start()

        Thread {
            process.waitFor()
            if (process.exitValue() != 0) return@Thread
            log.writeText(process.inputStream.bufferedReader().readText())
            if (callback != null) callback(process.exitValue())
        }.start()
    }
}