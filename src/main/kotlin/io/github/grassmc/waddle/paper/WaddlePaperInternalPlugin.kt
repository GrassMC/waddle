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

import io.github.grassmc.waddle.WaddlePlugin
import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import io.papermc.paperweight.userdev.PaperweightUserExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import kotlin.reflect.KClass

/**
 * A plugin that configures a Paper server with the Paperweight userdev environment.
 */
@Suppress("unused")
abstract class WaddlePaperInternalPlugin : WaddlePlugin<Project>() {
    override fun applyPlugins(): Iterable<KClass<out Plugin<*>>> =
        listOf(WaddlePaperPlugin::class, PaperweightUser::class)

    override fun init(target: Project) {
        target.configureDependencies()
    }

    private fun Project.configureDependencies() {
        extensions.configure<PaperweightUserExtension> {
            injectPaperRepository.set(false)
        }
        val minecraftVersion = extensions.getByType<WaddlePaperExtension>().minecraftVersion.get()
        dependencies {
            extensions.getByType<PaperweightUserDependenciesExtension>()
                .paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
        }
        plugins.withType<WaddlePaperPlugin> {
            configurations.named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).configure {
                dependencies.remove(paperApiDependency)
            }
        }
    }
}
