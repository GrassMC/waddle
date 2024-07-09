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

package io.github.grassmc.waddle.publish

import com.vanniktech.maven.publish.MavenPublishPlugin
import io.github.grassmc.waddle.WaddlePlugin
import org.gradle.api.Project

/** This class represents the wrapper for the `com.vanniktech.maven.publish` plugin. */
@Suppress("unused")
abstract class WaddlePublishPlugin : WaddlePlugin<Project>() {
    override fun applyPlugins() = setOf(MavenPublishPlugin::class)

    override fun init(target: Project) = Unit
}
