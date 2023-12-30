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
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

@CacheableTask
abstract class FindEntryNamespacesTask : DefaultTask() {
    @get:Input
    abstract val baseClasses: ListProperty<String>

    @get:InputDirectory
    @get:IgnoreEmptyDirectories
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val namespacesDir: DirectoryProperty

    @get:OutputFile
    abstract val foundedNamespace: RegularFileProperty

    @TaskAction
    fun find() {
        if (namespacesDir.map { it.asFile.exists() }.orNull == false) {
            return
        }

        val baseClasses = baseClasses.get()
        val founded = namespacesDir.asFileTree.asSequence().filter { it.isFile }.find { file ->
            val lines = file.readLines()
            lines.any { it in baseClasses }
        }
        val foundedNamespace = foundedNamespace.get().asFile.toPath()
        if (founded != null) {
            foundedNamespace.writeText(founded.name)
        } else {
            foundedNamespace.deleteIfExists()
        }
    }

    companion object {
        internal const val FIND_MAIN_ENTRY_TASK_NAME = "findMainEntry"
        internal const val FIND_BOOTSTRAPPER_ENTRY_TASK_NAME = "findBootstrapperEntry"

        internal val DEFAULT_MAIN_BASE_CLASSES = listOf(
            "org/bukkit/plugin/java/JavaPlugin",
            "org/bukkit/plugin/Plugin",
            "com/github/shynixn/mccoroutine/bukkit/SuspendingPlugin",
            "com/github/shynixn/mccoroutine/bukkit/SuspendingJavaPlugin",
            "com/github/shynixn/mccoroutine/folia/SuspendingPlugin",
            "com/github/shynixn/mccoroutine/folia/SuspendingJavaPlugin",
        )
        internal val DEFAULT_BOOTSTRAPPER_BASE_CLASSES = listOf("io/papermc/paper/plugin/bootstrap/PluginBootstrap")
    }
}

internal fun Project.registerFindMainEntryTask() =
    tasks.register<FindEntryNamespacesTask>(FindEntryNamespacesTask.FIND_MAIN_ENTRY_TASK_NAME) {
        group = PaperPluginPart.TASK_GROUP
        description = "Finds the main entry namespace for Paper plugin"
    }

internal fun Project.registerFindBootstrapEntryTask() =
    tasks.register<FindEntryNamespacesTask>(FindEntryNamespacesTask.FIND_BOOTSTRAPPER_ENTRY_TASK_NAME) {
        group = PaperPluginPart.TASK_GROUP
        description = "Finds the bootstrapper entry namespace for Paper plugin"
    }

internal fun Project.configureFindMainEntryTask(collectBaseClassesTask: TaskProvider<CollectBaseClassesTask>) =
    tasks.named<FindEntryNamespacesTask>(FindEntryNamespacesTask.FIND_MAIN_ENTRY_TASK_NAME) {
        dependsOn(collectBaseClassesTask)
        baseClasses.convention(FindEntryNamespacesTask.DEFAULT_MAIN_BASE_CLASSES)
        namespacesDir.convention(collectBaseClassesTask.flatMap { it.destinationDir })
        val defaultFoundedNamespace = objects.fileProperty()
        defaultFoundedNamespace.set(temporaryDir.resolve("main"))
        foundedNamespace.convention(defaultFoundedNamespace)
    }

internal fun Project.configureFindBootstrapperEntryTask(collectBaseClassesTask: TaskProvider<CollectBaseClassesTask>) =
    tasks.named<FindEntryNamespacesTask>(FindEntryNamespacesTask.FIND_BOOTSTRAPPER_ENTRY_TASK_NAME) {
        dependsOn(collectBaseClassesTask)
        baseClasses.convention(FindEntryNamespacesTask.DEFAULT_BOOTSTRAPPER_BASE_CLASSES)
        namespacesDir.convention(collectBaseClassesTask.flatMap { it.destinationDir })
        val defaultFoundedNamespace = objects.fileProperty()
        defaultFoundedNamespace.set(temporaryDir.resolve("bootstrapper"))
        foundedNamespace.convention(defaultFoundedNamespace)
    }
