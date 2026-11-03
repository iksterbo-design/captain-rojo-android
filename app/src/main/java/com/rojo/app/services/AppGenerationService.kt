package com.rojo.app.services

import com.rojo.app.models.AdScene
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

/**
 * Generation runbook states.
 *
 * Loading -> pipeline stage is being kicked off.
 * Success  -> logo and video URIs are ready (artifact paths of the local tool
 *             chain; the app copies them into its own filesDir for the bundle).
 * Error    -> one of the tool calls failed; [ToolRunner.lastError] holds detail.
 */
sealed class GenerationResult {
    object Loading : GenerationResult()
    data class Success(val logoUri: String, val videoUri: String, val note: String) : GenerationResult()
    data class Error(val message: String) : GenerationResult()
}

/** Defaults shared by the generation runbook, so every screen agrees on plan/length. */
object GenerationDefaults {
    const val DEFAULT_SCENE_PLAN = "1. Hook\n2. Body\n3. CTA"
    const val DEFAULT_LENGTH = "20s"
}

/** Runbook for creating a logo + ad video for a Captain Rojo project. */
interface GenerationService {
    /**
     * Full pipeline: generate the logo (logo_gen.py local) and render the ad
     * (ad_gen.py local) in one call. length and scenePlan fall back to the
     * defaults, so the ad studio can call startGenerate(prompt, style) with
     * no extras while the mapping screens can pass custom values.
     */
    fun startGenerate(
        prompt: String,
        style: String,
        length: String = GenerationDefaults.DEFAULT_LENGTH,
        scenePlan: String = GenerationDefaults.DEFAULT_SCENE_PLAN
    ): Flow<GenerationResult>

    /** Ad-only render: reuses the project's scenes as the scene plan. */
    fun startRender(scenes: List<AdScene>, length: String): Flow<GenerationResult>
}

class LocalGenerationService(
    private val toolRunner: ToolRunner,
    private val outputDir: File = File(toolRunner.toolsDir, "captain_rojo")
) : GenerationService {

    init {
        outputDir.mkdirs()
    }

    override fun startGenerate(
        prompt: String,
        style: String,
        length: String,
        scenePlan: String
    ): Flow<GenerationResult> = flow {
        emit(GenerationResult.Loading)
        try {
            val logoUri = generateLogo(style)
            val videoUri = renderAd(scenes = emptyList(), length = length, scenePlan = scenePlan)
            emit(GenerationResult.Success(
                logoUri = logoUri,
                videoUri = videoUri,
                note = "Logo generated with logo_gen.py; ad rendered with ad_gen.py (local)."
            ))
        } catch (e: Throwable) {
            emit(GenerationResult.Error(e.message ?: "Generation failed"))
        }
    }

    override fun startRender(scenes: List<AdScene>, length: String): Flow<GenerationResult> = flow {
        emit(GenerationResult.Loading)
        try {
            val videoUri = renderAd(scenes = scenes, length = length, scenePlan = scenes.joinToString("/") { it.title })
            emit(GenerationResult.Success(
                logoUri = "",
                videoUri = videoUri,
                note = "Ad rendered locally; bundle the project's existing logo."
            ))
        } catch (e: Throwable) {
            emit(GenerationResult.Error(e.message ?: "Render failed"))
        }
    }

    private fun generateLogo(style: String): String {
        val out = File(outputDir, "logo_${sanitize(style)}.png")
        val result = toolRunner.run("logo_gen.py", "--tool", "local", "--style", style, "--file_out", out.path)
        require(result.isSuccess) { toolRunner.lastError ?: "logo generation failed" }
        return out.path
    }

    private fun renderAd(scenes: List<AdScene>, length: String, scenePlan: String): String {
        val out = File(outputDir, "ad_${sanitize(length)}.mp4")
        val result = toolRunner.run("ad_gen.py", "--tool", "local", "--length", length, "--scene_plan", scenePlan)
        require(result.isSuccess) { toolRunner.lastError ?: "ad render failed" }
        // The local renderer writes cinematic_placeholder.mp4 into the toolkit
        // dir and reports that path in its envelope, so report what the tool
        // itself produced rather than the file_out argument.
        result.getOrDefault(emptyMap())["file_out"] ?: out.path
    }

    private fun sanitize(value: String): String = value.replace("[^a-zA-Z0-9]".toRegex(), "_")
}
