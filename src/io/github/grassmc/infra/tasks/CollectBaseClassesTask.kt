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

import io.github.grassmc.infra.utils.isNestedClass
import io.github.grassmc.infra.utils.readBaseClasses
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import org.gradle.work.InputChanges
import java.io.File
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeLines

@CacheableTask
abstract class CollectBaseClassesTask : DefaultTask() {
    @get:CompileClasspath
    @get:SkipWhenEmpty
    abstract val classes: ConfigurableFileCollection

    @get:Input
    @get:Optional
    abstract val skipNestedClass: Property<Boolean>

    @get:OutputDirectory
    abstract val destinationDir: DirectoryProperty

    private val shouldSkipNested by lazy { skipNestedClass.getOrElse(true) }
    private val readClasses = mutableSetOf<String>()

    @TaskAction
    fun collect(changes: InputChanges) {
        logger.debug("Skip nested classes: $shouldSkipNested")
        changes.getFileChanges(classes).asSequence().filter { it.fileType == FileType.FILE }
            .filter { it.file.path.endsWith(CLASS_FILE_EXTENSION) }.forEach(::processClassChange)
    }

    private fun processClassChange(change: FileChange) {
        val className = change.normalizedPath.removeSuffix(CLASS_FILE_EXTENSION)
        if (shouldSkipNested && isNestedClass(className)) {
            return
        }
        if (className in readClasses) {
            return
        }
        readClasses += className
        processClassChangeType(change, className)
    }

    private fun processClassChangeType(change: FileChange, className: String) {
        when (change.changeType) {
            ChangeType.ADDED, ChangeType.MODIFIED -> processAddedOrModifiedFile(change, className)
            else -> processFileRemoved(className)
        }
    }

    private fun processFileRemoved(className: String) {
        destinationDir.getPath(className.namespace()).deleteIfExists()
            .also { logger.debug("Removed base classes for $className") }
    }

    private fun processAddedOrModifiedFile(change: FileChange, className: String) {
        val bases = collectBasesForClass(change.file)
        if (bases.isEmpty()) {
            return
        }

        destinationDir.getPath(className.namespace()).writeLines(bases)
        logger.debug("Collected base classes for $className")
    }

    private fun collectBasesForClass(classFile: File) =
        readBaseClasses(classFile.readBytes()).filterNot { shouldSkipNested && isNestedClass(it) }

    private fun DirectoryProperty.getPath(path: String) = file(path).get().asFile.toPath()

    private fun String.namespace(): String = this.replace('/', '.')

    companion object {
        internal const val DEFAULT_NAME = "collectBaseClasses"
        private const val CLASS_FILE_EXTENSION = ".class"
    }
}

internal fun Project.registerCollectBaseClassesTask() =
    tasks.register<CollectBaseClassesTask>(CollectBaseClassesTask.DEFAULT_NAME) {
        description = "Collects base classes from the project's classes directory."
    }

internal fun Project.configureCollectBaseClassesTask() =
    tasks.named<CollectBaseClassesTask>(CollectBaseClassesTask.DEFAULT_NAME) {
        classes.from(compiledClasses())
        skipNestedClass.convention(true)

        val defaultDestinationDir = objects.directoryProperty()
        defaultDestinationDir.set(temporaryDirFactory.create())
        destinationDir.convention(defaultDestinationDir)
    }

private fun Project.compiledClasses() =
    extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME).map { it.output.classesDirs }
