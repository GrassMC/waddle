/*
 * Copyright 2023 GrassMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.grassmc.infra.tasks

import groovy.json.JsonOutput
import io.github.grassmc.infra.modules.parts.PaperPluginPart
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.attributes.Category
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

@CacheableTask
abstract class GeneratePaperLibrariesJsonTask : DefaultTask() {
    @get:Input
    abstract val resolvedComponentResult: Property<ResolvedComponentResult>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val reposWithoutMavenCentral = collectMavenRepositories().also {
            debugCollectedData("Maven repositories", it.values)
        }
        val moduleVersions = collectDependencies().also {
            debugCollectedData("root dependencies", it)
        }
        val librariesJson = mapOf(
            "repositories" to reposWithoutMavenCentral,
            "dependencies" to moduleVersions
        )
        writeJsonToFile(JsonOutput.toJson(librariesJson))
    }

    private fun collectMavenRepositories() =
        project.repositories.filterIsInstance<MavenArtifactRepository>().associate { it.name to it.url.toString() }

    private fun collectDependencies() = resolvedComponentResult.get().collectModuleVersions()

    private fun writeJsonToFile(json: String) {
        logger.debug("Generated JSON: $json")
        val paperLibrariesJson = outputDir.file("paper-libraries.json").get().asFile
        paperLibrariesJson.writeText(json)
        logger.debug("Paper libraries JSON file generated at: ${paperLibrariesJson.path}")
    }

    private fun <T> debugCollectedData(logDescription: String, data: Collection<T>) {
        logger.debug("Collected $logDescription:")
        for (it in data) {
            logger.debug("    - {}", it)
        }
    }

    private fun ResolvedComponentResult.collectModuleVersions() =
        dependencies
            .asSequence()
            .filterIsInstance<ResolvedDependencyResult>()
            .filterNot { it.containsPlatformVariant() }
            .map { it.selected.moduleVersion.toString() }
            .toSet()

    private fun ResolvedDependencyResult.containsPlatformVariant() =
        selected.variants.any { it.displayName.contains(Category.REGULAR_PLATFORM) }

    companion object {
        internal const val DEFAULT_NAME = "generatePaperLibrariesJson"
    }
}

internal fun Project.registerGeneratePaperLibrariesJsonTask() =
    tasks.register<GeneratePaperLibrariesJsonTask>(GeneratePaperLibrariesJsonTask.DEFAULT_NAME) {
        group = PaperPluginPart.TASK_GROUP
        description = "Generates a json file contains repositories and dependencies in the project."
    }

internal fun Project.configureGeneratePaperLibrariesJsonTask(): TaskProvider<GeneratePaperLibrariesJsonTask> {
    val generatePaperLibrariesJson =
        tasks.named<GeneratePaperLibrariesJsonTask>(GeneratePaperLibrariesJsonTask.DEFAULT_NAME) {
            resolvedComponentResult.convention(paperLibsRootResolution)
            val defaultOutputDir = objects.directoryProperty()
            defaultOutputDir.set(temporaryDirFactory.create())
            outputDir.convention(defaultOutputDir)
        }
    project.tasks.named<Jar>("jar") {
        dependsOn(generatePaperLibrariesJson)
        from(generatePaperLibrariesJson.flatMap { it.outputDir })
    }
    return generatePaperLibrariesJson
}

private val Project.paperLibsRootResolution
    get() = configurations.named("paperLibs").map { it.incoming.resolutionResult.root }
