package com.rojo.app.models

/**
 * Domain model for a Captain Rojo project (a logo + optional ad workflow).
 */
/**
 * Domain model for a Captain Rojo project (a logo + optional ad workflow).
 *
 * Marked @Serializable so Projects round-trip through the JSON repository
 * (kotlinx.serialization). The public API is unchanged from the placeholder.
 */
@Serializable
data class Project(
    val id: String,
    val name: String,
    val logo: Logo? = null,
    val ad: Ad? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class Logo(val prompt: String, val style: String, val imageUri: String? = null)

@Serializable
data class AdScene(val title: String, val prompt: String)

@Serializable
data class Ad(val scenes: List<AdScene> = emptyList(), val videoUri: String? = null, val voiceover: String? = null)
