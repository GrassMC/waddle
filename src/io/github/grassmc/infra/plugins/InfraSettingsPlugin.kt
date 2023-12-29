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

package io.github.grassmc.infra.plugins

import io.github.grassmc.infra.modules.ModuleLoader
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply
import org.gradle.toolchains.foojay.FoojayToolchainsConventionPlugin
import java.nio.file.Path
import kotlin.io.path.absolute

@Suppress("unused")
abstract class InfraSettingsPlugin : Plugin<Settings> {
    override fun apply(seetings: Settings) {
        seetings.plugins.apply(FoojayToolchainsConventionPlugin::class)

        val modules = ModuleLoader(seetings).loadModules().toSet()
        seetings.applyModulePlugin(modules)
    }

    private fun Settings.applyModulePlugin(moduleDirs: Set<Path>) {
        gradle.beforeProject {
            if (canApplyModule(moduleDirs)) {
                plugins.apply(InfraProjectPlugin::class)
            }
        }
    }

    private fun Project.canApplyModule(moduleDirs: Set<Path>) = projectDir.toPath().absolute() in moduleDirs
}
