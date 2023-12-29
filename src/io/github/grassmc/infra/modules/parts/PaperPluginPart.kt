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

package io.github.grassmc.infra.modules.parts

import io.github.grassmc.infra.modules.BindingPartCtx
import io.github.grassmc.infra.modules.ModuleBindingPart
import io.github.grassmc.infra.modules.ModuleType
import io.github.grassmc.infra.tasks.*
import org.gradle.api.Transformer
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

class PaperPluginPart(ctx: BindingPartCtx) : ModuleBindingPart by ctx {
    override val needToApply get() = manifest.type == ModuleType.PAPER_PLUGIN

    override fun beforeEvaluate() {
        registerPaperLibConfiguration()
        project.registerCollectBaseClassesTask()
        project.registerGeneratePaperPluginLoaderTask()
        project.registerGeneratePaperLibrariesJsonTask()
        project.registerFindMainEntryTask()
        project.registerFindBootstrapEntryTask()
    }

    override fun afterEvaluate() {
        project.configureGeneratePaperLibrariesJsonTask()
        val collectBaseClasses = project.configureCollectBaseClassesTask()
        val findMainEntry = project.configureFindMainEntryTask(collectBaseClasses)
        val findBootstrapperEntry = project.configureFindBootstrapperEntryTask(collectBaseClasses)
        val generatePaperPluginLoader = project.configureGeneratePaperPluginLoaderTask()
        project.tasks.withType<ProcessResources>().configureEach {
            dependsOn(findMainEntry, findBootstrapperEntry, generatePaperPluginLoader)
            outputs

            filesMatching("paper-plugin.yml") {
                filter(mainNamespaceFilter(findMainEntry))
                filter(bootstrapperNamespaceFilter(findBootstrapperEntry))
                filter(loaderNamespaceFilter(generatePaperPluginLoader))
                expand(prepareProps())
            }
        }
    }

    private fun prepareProps(): MutableMap<String, Any> {
        val props = mutableMapOf<String, Any>()
        props["name"] = project.name
        props["rootName"] = project.rootProject.name
        props["version"] = project.version
        project.description?.let { props["description"] = it }
        project.findProperty("website")?.let { props["website"] = it }
        project.findProperty("authors")?.let { prop -> props["authors"] = prop.toString().split(",").map { it.trim() } }
        project.findProperty("contributors")
            ?.let { prop -> props["contributors"] = prop.toString().split(",").map { it.trim() } }
        return props
    }

    private fun loaderNamespaceFilter(generatePaperPluginLoader: TaskProvider<GeneratePaperPluginLoaderTask>): Transformer<String?, String> =
        Transformer { line ->
            if (line.contains("loader: \$loader")) {
                val packageName = generatePaperPluginLoader.flatMap { it.packageName }.get()
                val className = generatePaperPluginLoader.flatMap { it.className }.get()
                "loader: ${packageName}.${className}"
            } else {
                line
            }
        }

    private fun bootstrapperNamespaceFilter(findBootstrapperEntry: TaskProvider<FindEntryNamespacesTask>): Transformer<String?, String> =
        Transformer { line ->
            val bootstrapperEntry = findBootstrapperEntry.flatMap { it.foundedNamespace }.map { it.asFile }
            if (line.contains("bootstrap: \$bootstrapper")) {
                check(bootstrapperEntry.orNull?.exists() == true) { "Could not find bootstrapper namespace for Paper plugin" }
                "bootstrapper: ${bootstrapperEntry.map { it.readText() }.get()}"
            } else {
                line
            }
        }

    private fun mainNamespaceFilter(findMainEntry: TaskProvider<FindEntryNamespacesTask>): Transformer<String?, String> =
        Transformer { line ->
            val mainEntry = findMainEntry.flatMap { it.foundedNamespace }.map { it.asFile }
            if (line.contains("main: \$main")) {
                check(mainEntry.orNull?.exists() == true) { "Could not find main namespace for Paper plugin" }
                "main: ${mainEntry.map { it.readText() }.get()}"
            } else {
                line
            }
        }

    private fun registerPaperLibConfiguration() {
        project.configurations.register(PAPER_LIB_CONFIGURATION_NAME) {
            project.configurations.getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME).extendsFrom(this)
        }
    }

    companion object {
        const val PAPER_LIB_CONFIGURATION_NAME = "paperLib"

        const val TASK_GROUP = "paper plugin"
    }
}
