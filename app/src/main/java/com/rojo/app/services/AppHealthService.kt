package com.rojo.app.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Result states for the environment health check.
 */
sealed class HealthResult {
    object Loading : HealthResult()
    data class Success(val result: Map<String, String>) : HealthResult()
    data class Error(val message: String) : HealthResult()
}

/**
 * Runbook: shell out to tools/health_check.py and surface the version strings
 * (ffmpeg, ffprobe, piper, cloudflared, python) plus the config paths the
 * health check reports.
 */
interface HealthService {
    fun runCheck(): Flow<HealthResult>
}

class LocalHealthService(private val toolRunner: ToolRunner) : HealthService {
    override fun runCheck(): Flow<HealthResult> = flow {
        emit(HealthResult.Loading)
        try {
            val result = toolRunner.run("health_check.py")
            if (result.isFailure) {
                throw IllegalStateException(toolRunner.lastError ?: "health check failed")
            }
            emit(HealthResult.Success(result.getOrDefault(emptyMap())))
        } catch (e: Throwable) {
            emit(HealthResult.Error(e.message ?: "Health check failed"))
        }
    }
}
