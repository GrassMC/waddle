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
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.process.JavaDebugOptions
import org.jetbrains.kotlin.gradle.plugin.HasProject
import xyz.jpenilla.runpaper.task.RunServer
import xyz.jpenilla.runtask.pluginsapi.DownloadPluginsSpec
import javax.inject.Inject

internal abstract class InternalDebugServerOptions
    @Inject
    constructor(
        final override val project: Project,
        private val runServerTask: TaskProvider<RunServer>,
    ) : DebugServerOptions,
        HasProject {
        override fun downloadPlugins(spec: DownloadPluginsSpec.() -> Unit) =
            project.afterEvaluate {
                runServerTask.configure {
                    downloadPlugins(spec)
                }
            }

        override fun options(options: JavaDebugOptions.() -> Unit) =
            runServerTask.configure {
                debugOptions.options()
            }

        override fun server(
            host: String,
            port: Int,
        ) = options {
            enabled.set(true)
            @Suppress("UnstableApiUsage")
            this.host.set(host)
            this.port.set(port)
            server.set(true)
            suspend.set(false)
        }
    }
