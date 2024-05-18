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

package io.github.grassmc.waddle.paper

import org.gradle.api.provider.Property
import org.gradle.process.JavaDebugOptions
import xyz.jpenilla.runtask.pluginsapi.DownloadPluginsSpec

/**
 * Represents the DebugServerOptions to configure the debug server for the Paper development environment.
 */
interface DebugServerOptions {
    /**
     * Represents a property that indicates whether the end user has accepted the End User License Agreement (EULA).
     *
     * Please read the [EULA](https://aka.ms/MinecraftEULA) before setting this property to true.
     */
    val acceptEula: Property<Boolean>

    /*
     * Configures the download of plugins for the debug server.
     */
    fun downloadPlugins(spec: DownloadPluginsSpec.() -> Unit)

    /**
     * Configures the Java debug options for the debug server.
     */
    fun options(options: JavaDebugOptions.() -> Unit)

    /**
     * Configures the Java debug options as a debug server.
     */
    fun server(
        host: String = "localhost",
        port: Int = 5005,
    )
}
