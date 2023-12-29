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

import io.github.grassmc.infra.dsl.PaperRepositoriesExtension
import io.github.grassmc.infra.modules.BindingPartCtx
import io.github.grassmc.infra.modules.ModuleBindingPart
import io.github.grassmc.infra.modules.ModuleType
import io.github.grassmc.infra.modules.settings
import io.github.grassmc.infra.modules.settings.PaperVersion
import io.github.grassmc.infra.utils.extensions
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.create

open class PaperLibPart(ctx: BindingPartCtx) : ModuleBindingPart by ctx {
    private val paperSettings get() = settings.paper

    override val needToApply: Boolean
        get() = manifest.type == ModuleType.PAPER_LIB || manifest.type == ModuleType.PAPER_PLUGIN

    override fun beforeEvaluate() {
        project.repositories.extensions.create<PaperRepositoriesExtension>("paper", project).paperMC()
        val paperVersion = paperSettings?.version?.artifactVersion ?: PaperVersion.DEFAULT.artifactVersion
        val paperApiNotation = "io.papermc.paper:paper-api:$paperVersion"
        project.dependencies.add(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, paperApiNotation)
    }
}
