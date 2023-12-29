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
import io.github.grassmc.infra.tasks.AcceptEula
import io.github.grassmc.infra.tasks.registerAcceptEulaTask
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.named
import xyz.jpenilla.runpaper.RunPaperPlugin
import xyz.jpenilla.runpaper.task.RunServer

class RunPaperPart(ctx: BindingPartCtx) : ModuleBindingPart by ctx {
    override val needToApply: Boolean get() = manifest.type == ModuleType.PAPER_PLUGIN

    override fun beforeEvaluate() {
        project.plugins.apply(RunPaperPlugin::class)
        project.registerAcceptEulaTask()
    }

    override fun afterEvaluate() {
        val runPaperDir = project.layout.projectDirectory.dir(RUN_PAPER_DIR)
        val acceptEula = project.tasks.named<AcceptEula>(AcceptEula.DEFAULT_NAME) {
            eulaAgree.convention((manifest.settings?.paper?.acceptEula ?: false))
            eulaFile.convention(runPaperDir.file("eula.txt"))
        }
        project.tasks.named<RunServer>("runServer") {
            dependsOn(acceptEula)

            minecraftVersion(manifest.settings?.paper?.version?.minecraftVersion ?: "1.20.4")
            runDirectory(runPaperDir.asFile)
        }
    }

    companion object {
        const val RUN_PAPER_DIR = ".run-paper"
    }
}
