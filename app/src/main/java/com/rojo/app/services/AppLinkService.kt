package com.rojo.app.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Result states for the Falcon / Play Store linking runbook.
 *
 * Success, NeedsOAuth, NeedsInstallPermission, Error — the four terminal
 * states the linking screen reacts to.
 */
sealed class LinkResult {
    data class NeedsOAuth(val message: String) : LinkResult()
    data class NeedsInstallPermission(val message: String) : LinkResult()
    data class Success(val cause: String, val actions: List<String>, val context: Map<String, String>) : LinkResult()
    data class Error(val message: String) : LinkResult()
}

/**
 * Runbook for linking a device to the Play Store (Falcon).
 *
 * Steps driven from the toolkit, in order:
 *   1. health_check.py  -> config path + cloud credentials (OAuth prerequisite)
 *   2. playstore_link.py -> root_cause classification -> install-permission fix
 *
 * Interactive steps (copy the device code, grant Install unknown apps) are
 * human actions; this service drives the diagnostics and exposes the exact
 * actions list the tool computed.
 */
interface LinkService {
    fun startLink(email: String, device: String, errorText: String?): Flow<LinkResult>
}

class LocalLinkService(private val toolRunner: ToolRunner) : LinkService {

    override fun startLink(email: String, device: String, errorText: String?): Flow<LinkResult> = flow {
        val emailNorm = email.trim()
        val deviceNorm = device.trim()
        val errorNorm = errorText?.trim()

        val health = toolRunner.run("health_check.py")
        if (health.isFailure) {
            emit(LinkResult.Error(healthError(health)))
            return@flow
        }
        val healthMap = health.getOrDefault(emptyMap())
        if (healthMap.isEmpty()) {
            emit(LinkResult.Error("health_check.py returned an empty envelope"))
            return@flow
        }

        // 1) Config path must exist before anything else.
        val configPresent = healthMap["config_exists"]?.jsonPrimitive?.boolean == true
        if (!configPresent) {
            emit(NeedsOAuth("Tooling config not found — set the app config path (config_status) before linking."))
            return@flow
        }

        // 2) Cloud credentials are the OAuth prerequisite.
        val envKeys = healthMap["env_keys"]?.jsonObject ?: emptyMap()
        val openRouter = envKeys["OPENROUTER_API_KEY"]?.jsonPrimitive?.content == "true"
        val nous = envKeys["NOUS_API_KEY"]?.jsonPrimitive?.content == "true"
        if (!openRouter || !nous) {
            val missing = mutableListOf<String>()
            if (!openRouter) missing += "OPENROUTER_API_KEY"
            if (!nous) missing += "NOUS_API_KEY"
            emit(NeedsOAuth("Missing cloud credentials: ${missing.joinToString()}. Set them in the app config before linking."))
            return@flow
        }

        // 3) Play Store linking diagnostics (root-cause classification).
        val link = toolRunner.run("playstore_link.py", "--error_text", errorNorm ?: "", "--device", deviceNorm, "--email", emailNorm)
        if (link.isFailure) {
            emit(LinkResult.Error(linkError(link)))
            return@flow
        }
        val linkMap = link.getOrDefault(emptyMap())
        val cause = linkMap["root_cause"]?.takeIf { it.isNotBlank() } ?: "unclassified"

        when (cause) {
            "insufficient install/security permission" -> {
                emit(NeedsInstallPermission("Install permission is blocked — enable Install unknown apps for the Play Store app and retry."))
            }
            "OAuth state mismatch" -> {
                emit(NeedsOAuth("OAuth state mismatch — sign the device into Google Play with the matching email, then re-run the flow."))
            }
            "Google account / Play Services mismatch" -> {
                emit(NeedsOAuth("Google account / Play Services mismatch — add the Google account that matches the link email, then re-run the flow."))
            }
            "app not found / not installed / private listing" -> {
                emit(NeedsInstallPermission("Listing not available on the Play Store — install the exact public listing or ask the publisher for an alternate install path."))
            }
            else -> emit(LinkResult.Error("Unclassified linking problem: $cause"))
        }
    }

    private fun healthError(result: Result<Map<String, String>>): String =
        toolRunner.lastError ?: result.exceptionOrNull()?.message ?: "health check failed"

    private fun linkError(result: Result<Map<String, String>>): String =
        toolRunner.lastError ?: result.exceptionOrNull()?.message ?: "link diagnostic failed"
}
