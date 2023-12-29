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

import io.github.grassmc.infra.modules.parts.PaperPluginPart
import io.github.grassmc.infra.plugins.InfraProjectPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

@CacheableTask
abstract class GeneratePaperPluginLoaderTask : DefaultTask() {
    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val className: Property<String>

    @get:OutputDirectory
    abstract val generatedDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val generated = generatedDir.get().asFile
        val templateIn = requireNotNull(InfraProjectPlugin::class.java.getResourceAsStream(TEMPLATE_RESOURCE_PATH)) {
            "Template not found"
        }
        val template = templateIn.use { stream -> stream.bufferedReader().use { it.readText() } }
        val destinationDir = generated.resolve(packageName.get().replace('.', '/'))
        destinationDir.mkdirs()
        destinationDir.resolve("${className.get()}.java").writeText(template.replaceTokens())
    }

    private fun String.replaceTokens() = replace("%%PACKAGE%%", packageName.get()).replace("%%CLASS%%", className.get())

    companion object {
        private const val TEMPLATE_RESOURCE_PATH = "/templates/GrassPluginLoader.java.template"
        internal const val DEFAULT_NAME = "generatePaperPluginLoader"
    }
}

internal fun Project.registerGeneratePaperPluginLoaderTask() =
    tasks.register<GeneratePaperPluginLoaderTask>(GeneratePaperPluginLoaderTask.DEFAULT_NAME) {
        group = PaperPluginPart.TASK_GROUP
        description = "Generate a plugin loader to load plugin libraries"
    }

internal fun Project.configureGeneratePaperPluginLoaderTask(): TaskProvider<GeneratePaperPluginLoaderTask> {
    val generatePaperPluginLoader =
        tasks.named<GeneratePaperPluginLoaderTask>(GeneratePaperPluginLoaderTask.DEFAULT_NAME) {
            packageName.convention("io.github.grassmc.paper.plugin")
            className.convention("GrassPluginLoader")
            generatedDir.convention(project.layout.buildDirectory.dir("generated/sources/$name"))
        }
    project.tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME) {
        dependsOn(generatePaperPluginLoader)
    }
    project.extensions.configure<SourceSetContainer> {
        named("main") {
            java.srcDir(generatePaperPluginLoader.flatMap { it.generatedDir })
        }
    }
    return generatePaperPluginLoader
}
