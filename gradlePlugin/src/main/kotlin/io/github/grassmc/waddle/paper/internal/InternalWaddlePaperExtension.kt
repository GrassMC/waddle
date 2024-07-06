/*
 * Copyright 2024 GrassMC
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

package io.github.grassmc.waddle.paper.internal

import io.github.grassmc.waddle.paper.DebugServerOptions
import io.github.grassmc.waddle.paper.WaddlePaperExtension
import io.github.grassmc.waddle.paper.WaddlePaperPlugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import org.jetbrains.kotlin.gradle.plugin.HasProject
import org.jetbrains.kotlin.gradle.utils.named
import xyz.jpenilla.runpaper.task.RunServer

internal abstract class InternalWaddlePaperExtension(
    final override val project: Project,
    objects: ObjectFactory,
) : WaddlePaperExtension,
    HasProject {
    internal val debugServerOptions =
        objects.newInstance<InternalDebugServerOptions>(
            project,
            project.tasks.named<RunServer>(WaddlePaperPlugin.RUN_SERVER_TASK_NAME),
        )

    abstract override val minecraftVersion: Property<String>

    override fun debugServer(options: DebugServerOptions.() -> Unit) = debugServerOptions.options()
}
