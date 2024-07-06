/*
 * Copyright 2024 GrassMC
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

package io.github.grassmc.waddle.paper

import io.github.grassmc.waddle.DEFAULT_MINECRAFT_VERSION
import io.github.grassmc.waddle.WaddlePlugin
import io.github.grassmc.waddle.paper.extensions.paperApi
import io.github.grassmc.waddle.paper.extensions.paperMc
import io.github.grassmc.waddle.paper.internal.InternalWaddlePaperExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.expand
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.repositories
import org.gradle.language.jvm.tasks.ProcessResources
import xyz.jpenilla.runpaper.RunPaperPlugin
import xyz.jpenilla.runpaper.task.RunServer
import kotlin.reflect.KClass

/**
 * A plugin that configures a Paper server for running and debugging.
 */
abstract class WaddlePaperPlugin : WaddlePlugin<Project>() {
    internal var paperApiDependency: Dependency? = null

    override fun applyPlugins(): Iterable<KClass<out Plugin<*>>> = listOf(JavaPlugin::class, RunPaperPlugin::class)

    override fun init(target: Project) {
        val waddlePaper = target.createWaddlePaperExtension()
        target.configureDependencies(waddlePaper.minecraftVersion.orNull ?: DEFAULT_MINECRAFT_VERSION)
        target.configureRunServerTasks(waddlePaper)
        target.setupProcessPluginYml()
    }

    private fun Project.configureDependencies(minecraftVersion: String) {
        repositories {
            paperMc()
        }
        dependencies {
            paperApiDependency = add(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, paperApi(minecraftVersion))
        }
    }

    private fun Project.configureRunServerTasks(waddlePaper: WaddlePaperExtension) {
        tasks.named<RunServer>(RUN_SERVER_TASK_NAME) {
            minecraftVersion(waddlePaper.minecraftVersion.orNull ?: DEFAULT_MINECRAFT_VERSION)
            runDirectory(file(RUN_SERVER_DIR_NAME))
            if (waddlePaper is InternalWaddlePaperExtension) {
                systemProperties[MOJANG_AGREE_EULA_PROPERTY] = waddlePaper.debugServerOptions.acceptEula.orNull ?: false
            }
        }
    }

    private fun Project.createWaddlePaperExtension() =
        extensions
            .create(WaddlePaperExtension::class, WADDLE_PAPER_EXTENSION_NAME, InternalWaddlePaperExtension::class)
            .apply { minecraftVersion.convention(DEFAULT_MINECRAFT_VERSION) }

    private fun Project.setupProcessPluginYml() {
        val processPaperPluginYml =
            tasks.register<ProcessResources>(PROCESS_PLUGIN_YML_TASK_NAME)
        afterEvaluate {
            val mainSourceSet = extensions.getByType<SourceSetContainer>()[SourceSet.MAIN_SOURCE_SET_NAME]
            processPaperPluginYml.configure {
                mustRunAfter(JavaPlugin.PROCESS_RESOURCES_TASK_NAME)
                from(mainSourceSet.resources)
                filesMatching(listOf("paper-plugin.yml", "plugin.yml")) {
                    filteringCharset = Charsets.UTF_8.name()
                    expand(
                        "name" to project.name,
                        "version" to project.version.toString(),
                    )
                }
                mainSourceSet.output.resourcesDir?.let { into(it) }
            }
            tasks.named(JavaPlugin.JAR_TASK_NAME) {
                dependsOn(processPaperPluginYml)
            }
        }
    }

    companion object {
        internal const val WADDLE_PAPER_EXTENSION_NAME = "paper"

        internal const val RUN_SERVER_TASK_NAME = "runServer"
        private const val RUN_SERVER_DIR_NAME = ".run-server"
        private const val MOJANG_AGREE_EULA_PROPERTY = "com.mojang.eula.agree"

        internal const val PROCESS_PLUGIN_YML_TASK_NAME = "processPluginYml"
    }
}
