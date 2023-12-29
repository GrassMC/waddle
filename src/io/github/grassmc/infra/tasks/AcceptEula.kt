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

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.register

@CacheableTask
abstract class AcceptEula : DefaultTask() {
    @get:Input
    @get:Optional
    abstract val eulaAgree: Property<Boolean>

    @get:OutputFile
    abstract val eulaFile: RegularFileProperty

    @TaskAction
    fun acceptEula() {
        if (eulaAgree.orNull == true) {
            eulaFile.get().asFile.writeText("eula=true")
        }
    }

    companion object {
        const val DEFAULT_NAME = "acceptEula"
    }
}

internal fun Project.registerAcceptEulaTask() = tasks.register<AcceptEula>(AcceptEula.DEFAULT_NAME) {
    group = "run paper"
}
