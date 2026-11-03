package com.rojo.app.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.parseToJsonElement

/**
 * Single source of truth for JSON plumbing.
 *
 * Domain models (Project/Logo/Ad) are encoded by the repository with
 * kotlinx.serialization. The Python tool envelopes (ad_gen / logo_gen /
 * health_check / playstore_link / tts_voiceover) are parsed here, so no other
 * layer re-implements envelope parsing.
 */
val json = Json { ignoreUnknownKeys = true }

fun String.parseEnvelope(): JsonObject = json.parseToJsonElement(this) as? JsonObject
    ?: JsonObject(emptyMap())

fun JsonObject.isOk(): Boolean = get("ok")?.jsonPrimitive?.boolean == true

fun JsonObject.errorOrNull(): String? = get("error")?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }

fun JsonObject.resultMap(): Map<String, JsonElement> = get("result")?.jsonObject ?: emptyMap()

/**
 * Converts a tool envelope's result object into a flat String map.
 *
 * Values that are not JSON primitives (null, objects, arrays, e.g. the
 * health check's `cloudflared: null` or `env_keys` object) become "" so the
 * rest of the app never sees non-stringizable values.
 */
fun JsonObject.toUntypedMap(): Map<String, String> = mapValues { (_, value) ->
    if (value is JsonPrimitive) value.content else ""
}

fun JsonObject.getString(key: String, default: String? = null): String? =
    get(key)?.jsonPrimitive?.content?.takeIf { it.isNotBlank() } ?: default

fun JsonObject.getBoolean(key: String, default: Boolean = false): Boolean =
    get(key)?.jsonPrimitive?.boolean ?: default
