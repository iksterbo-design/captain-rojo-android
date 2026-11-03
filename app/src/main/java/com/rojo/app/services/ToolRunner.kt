package com.rojo.app.services

import java.io.ByteArrayOutputStream
import java.io.File
import com.rojo.app.utils.parseEnvelope
import com.rojo.app.utils.toUntypedMap

/**
 * Single place that shells out to the Captain Rojo toolkit scripts
 * (tools/ad_gen.py, tools/logo_gen.py, tools/health_check.py,
 * tools/playstore_link.py, tools/tts_voiceover.py).
 *
 * One ProcessBuilder block for the whole app, so no other layer re-implements
 * tool invocation. The caller must ensure `python3` is on PATH (or provide a
 * resolved command).
 */
class ToolRunner(val toolsDir: File) {

    @Volatile
    private var lastError: String? = null

    /**
     * Runs `python3 <toolsDir>/<script> <args...>`, parses the JSON envelope
     * the toolkit scripts emit, and returns the `result` object as a
     * Map<String, String>. Failure is reported through
     * [lastError] and a failed [Result].
     */
    fun run(script: String, vararg args: String): Result<Map<String, String>> {
        val command = listOf("python3", File(toolsDir, script).path) + args.toList()
        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()

        val out = ByteArrayOutputStream()
        process.inputStream.copyTo(out)
        val finished = process.waitFor()

        val text = out.toString(Charsets.UTF_8).trim()
        if (finished != 0) {
            lastError = text.takeIf { it.isNotEmpty() } ?: "tool exited with code $finished"
            return Result.failure(IllegalStateException(lastError!!))
        }
        if (text.isEmpty()) {
            lastError = "tool produced no output"
            return Result.failure(IllegalStateException(lastError!!))
        }

        return try {
            val envelope = text.parseEnvelope()
            if (!envelope.isOk()) {
                lastError = envelope.errorOrNull() ?: "tool reported failure"
                Result.failure(IllegalStateException(lastError!!))
            } else {
                Result.success(envelope.resultMap().toUntypedMap())
            }
        } catch (parse: Exception) {
            lastError = "could not parse tool output: ${parse.message}"
            Result.failure(parse)
        }
    }

    val lastError: String?
        get() = lastError
}
