package com.rojo.app.di

import android.content.Context
import com.rojo.app.repository.ProjectRepository
import com.rojo.app.services.LocalGenerationService
import com.rojo.app.services.LocalHealthService
import com.rojo.app.services.LocalLinkService
import com.rojo.app.services.ToolRunner
import com.rojo.app.services.GenerationService
import com.rojo.app.services.LinkService
import com.rojo.app.services.HealthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI module: wires the single service layer (one generation service,
 * one link service, one health service, one repository).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideToolRunner(@ApplicationContext context: Context): ToolRunner =
        ToolRunner(toolkitDir(context))

    /**
     * Locates the Python toolkit directory.
     *
     * Sandbox: C:\Users\ikste\CaptainRojo-Toolkit\tools (relative to this
     * machine). On a real device the scripts ship inside the APK (assets/ or
     * filesDir/tools) and the same lookup applies.
     */
    private fun toolkitDir(context: Context): File {
        val candidates = listOf(
            File(context.filesDir, "tools"),
            File("C:/Users/ikste/CaptainRojo-Toolkit/tools")
        )
        return candidates.firstOrNull { it.isDirectory } ?: File(context.filesDir, "tools")
    }

    @Provides
    fun provideGenerationService(
        @ApplicationContext context: Context,
        toolRunner: ToolRunner
    ): GenerationService = LocalGenerationService(
        toolRunner,
        File(context.filesDir, "captain_rojo_gen")
    )

    @Provides
    fun provideLinkService(@ApplicationContext context: Context, toolRunner: ToolRunner): LinkService =
        LocalLinkService(toolRunner)

    @Provides
    fun provideHealthService(@ApplicationContext context: Context, toolRunner: ToolRunner): HealthService =
        LocalHealthService(toolRunner)

    @Provides
    fun provideProjectRepository(@ApplicationContext context: Context): ProjectRepository =
        ProjectRepository(context)
}
