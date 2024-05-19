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

package io.github.grassmc.waddle.shadow

import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import io.github.grassmc.waddle.WaddlePlugin
import io.github.grassmc.waddle.paper.WaddlePaperPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * A plugin that applies the [ShadowJavaPlugin] and configures task dependencies
 */
@Suppress("unused")
abstract class WaddleShadowPlugin : WaddlePlugin<Project>() {
    override fun applyPlugins() = setOf(ShadowJavaPlugin::class)

    override fun init(target: Project) {
        target.configureTaskDependencies()
    }

    private fun Project.configureTaskDependencies() {
        tasks.named(LifecycleBasePlugin.BUILD_TASK_NAME) {
            dependsOn(ShadowJavaPlugin.SHADOW_JAR_TASK_NAME)
        }

        plugins.withType<WaddlePaperPlugin> {
            tasks.named(ShadowJavaPlugin.SHADOW_JAR_TASK_NAME) {
                dependsOn(WaddlePaperPlugin.PROCESS_PLUGIN_YML_TASK_NAME)
            }
        }
    }
}
