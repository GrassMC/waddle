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

package io.github.grassmc.infra.modules

import org.gradle.api.initialization.Settings
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolute
import kotlin.io.path.pathString
import kotlin.io.path.walk

@OptIn(ExperimentalPathApi::class)
internal class ModuleLoader(private val settings: Settings) {
    fun loadModules(): Sequence<Path> {
        val rootDir = settings.rootDir.toPath().absolute()
        return rootDir.walk().filter { it.isGrassModuleYaml }.map {
            val moduleDir = it.parent
            val moduleName = it.readModuleName()
            if (moduleDir == rootDir) {
                applyRootNameIfPresent(moduleName)
                return@map moduleDir
            }

            val subprojectName = createSubprojectName(moduleDir, rootDir, settings.rootProject.name, moduleName)
            settings.include(subprojectName)
            settings.project(":$subprojectName").projectDir = moduleDir.toFile()
            moduleDir
        }
    }

    private fun applyRootNameIfPresent(moduleName: String?) {
        moduleName?.let { settings.gradle.settingsEvaluated { rootProject.name = it } }
    }

    private fun createSubprojectName(
        moduleDir: Path, rootDir: Path, rootProjectName: String, moduleName: String?
    ): String {
        val projectPath = rootDir.relativize(moduleDir).pathString.replace('/', ':')
        val candidateSubprojectName = moduleName ?: projectPath
        return candidateSubprojectName.replace("\$root", rootProjectName).replace("\$projectPath", projectPath)
            .replace("\$project", projectPath.replace(':', '-'))
    }
}
