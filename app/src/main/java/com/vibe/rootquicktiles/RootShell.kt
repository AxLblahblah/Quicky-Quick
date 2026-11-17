package com.vibe.rootquicktiles

import android.util.Log

object RootShell {
    private const val TAG = "RootShell"

    fun check(): Boolean = try {
        val process = ProcessBuilder("su", "-c", "id -u").start()
        val out = process.inputStream.bufferedReader().use { it.readText().trim() }
        process.waitFor()
        out.endsWith("0")
    } catch (e: Exception) {
        Log.w(TAG, "root check failed", e)
        false
    }

    fun run(command: String): String? = try {
        val process = ProcessBuilder("su", "-c", command).start()
        val out = process.inputStream.bufferedReader().use { it.readText() }
        process.errorStream.bufferedReader().use { it.readText() }
        process.waitFor()
        if (process.exitValue() == 0) out else null
    } catch (e: Exception) {
        Log.w(TAG, "command failed: $command", e)
        null
    }
}
