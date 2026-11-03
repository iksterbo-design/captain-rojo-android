package com.rojo.app.repository

import android.content.Context
import com.rojo.app.models.Project
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Data layer: single source of truth for project state.
 *
 * Projects are persisted with kotlinx.serialization JSON under
 * [Context.filesDir]/captain_rojo/<project-id>.json.
 *
 * The public API is unchanged from the placeholder implementation:
 * create / save / get / loadAll / delete.
 */
class ProjectRepository(private val context: Context) {

    private val storage = File(context.filesDir, "captain_rojo").apply { if (!exists()) mkdirs() }
    private val json = Json { ignoreUnknownKeys = true }

    fun create(name: String): Project {
        val project = Project(id = java.util.UUID.randomUUID().toString(), name = name)
        save(project)
        return project
    }

    fun save(project: Project) {
        val file = File(storage, "${project.id}.json")
        file.parentFile.mkdirs()
        file.writeText(json.encodeToString(Project.serializer(), project))
    }

    fun get(id: String): Project? {
        val file = File(storage, "$id.json")
        if (!file.isFile) return null
        return json.decodeFromString(file.readText())
    }

    fun loadAll(): List<Project> {
        val projects = mutableListOf<Project>()
        storage.listFiles()?.forEach { file ->
            if (file.isFile && file.name.endsWith(".json")) {
                try {
                    projects += json.decodeFromString(file.readText())
                } catch (_: Exception) {
                    // Corrupt file: skip, never crash the app.
                }
            }
        }
        return projects
    }

    fun delete(project: Project) {
        File(storage, "${project.id}.json").delete()
    }
}
