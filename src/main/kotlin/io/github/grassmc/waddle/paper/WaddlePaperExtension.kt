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

/**
 * Represents the WaddlePaperExtension to configure the Paper development environment
 */
interface WaddlePaperExtension {
    /**
     * The Minecraft version used for determining the Paper API version and debug server version.
     */
    val minecraftVersion: Property<String>

    /**
     * Configures the debug server options for the Paper development environment.
     */
    fun debugServer(options: DebugServerOptions.() -> Unit)
}
