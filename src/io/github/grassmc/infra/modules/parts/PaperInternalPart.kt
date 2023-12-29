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
import io.github.grassmc.infra.modules.settings
import io.github.grassmc.infra.modules.settings.PaperVersion
import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import io.papermc.paperweight.userdev.PaperweightUserExtension
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.*

class PaperInternalPart(ctx: BindingPartCtx) : ModuleBindingPart by ctx {
    private val paperSettings get() = settings.paper

    override val needToApply get() = manifest.type in ModuleType.PAPER_PLATFORMS && paperSettings?.internal == true

    override fun beforeEvaluate() {
        project.plugins.apply(PaperweightUser::class)
        val paperVersion = paperSettings?.version?.artifactVersion ?: PaperVersion.DEFAULT.artifactVersion
        project.configurations[JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME].dependencies.removeIf {
            it.group == "io.papermc.paper" && it.name == "paper-api" && it.version == paperVersion
        }
        project.extensions.configure<PaperweightUserExtension> {
            injectPaperRepository = false
        }
        project.dependencies.extensions.getByType<PaperweightUserDependenciesExtension>().paperDevBundle(paperVersion)
    }

    override fun afterEvaluate() {
        project.tasks.named(BasePlugin.ASSEMBLE_TASK_NAME) {
            dependsOn(project.tasks.named(REOBF_JAR_TASK_NAME))
        }
    }

    companion object {
        private const val REOBF_JAR_TASK_NAME = "reobfJar"
    }
}
