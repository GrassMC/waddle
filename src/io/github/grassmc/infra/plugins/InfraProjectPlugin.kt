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

import io.github.grassmc.infra.modules.BindingPartCtx
import io.github.grassmc.infra.modules.ModuleBindingPart
import io.github.grassmc.infra.modules.grassModuleYaml
import io.github.grassmc.infra.modules.parts.*
import io.github.grassmc.infra.modules.readModuleManifest
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class InfraProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val manifest = project.projectDir.toPath().grassModuleYaml.readModuleManifest()
        val moduleCtx = BindingPartCtx(project, manifest)
        val registeredParts = setOf(
            KotlinJvmPart(moduleCtx),
            PaperLibPart(moduleCtx),
            PaperPluginPart(moduleCtx),
            PaperInternalPart(moduleCtx),
            TestPart(moduleCtx),
            RunPaperPart(moduleCtx),
        )

        val appliedParts = registeredParts.filter { it.needToApply }
        appliedParts.forEach(ModuleBindingPart::beforeEvaluate)
        project.afterEvaluate {
            appliedParts.forEach(ModuleBindingPart::afterEvaluate)
        }
    }
}
