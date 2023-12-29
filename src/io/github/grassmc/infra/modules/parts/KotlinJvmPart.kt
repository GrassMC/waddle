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

import BuildConstants
import io.github.grassmc.infra.dsl.CommonRepositoriesExtension
import io.github.grassmc.infra.modules.BindingPartCtx
import io.github.grassmc.infra.modules.ModuleBindingPart
import io.github.grassmc.infra.modules.settings
import io.github.grassmc.infra.modules.settings.ToolChainsSettings
import io.github.grassmc.infra.utils.extensions
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KotlinJvmPart(private val ctx: BindingPartCtx) : ModuleBindingPart by ctx {
    private val toolChainsSettings get() = settings.toolchains ?: ToolChainsSettings()
    private val kotlinSettings get() = settings.kotlin

    override val needToApply get() = true

    override fun beforeEvaluate() {
        project.plugins.apply(KotlinPluginWrapper::class)

        project.repositories.mavenCentral()
        project.repositories.extensions.create<CommonRepositoriesExtension>("common", project)

        project.extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion = toolChainsSettings.javaLanguageVersion
                vendor = toolChainsSettings.vendor ?: ToolChainsSettings.DEFAULT_VENDOR
            }
        }
        project.extensions.configure<KotlinJvmProjectExtension> {
            sourceSets.all {
                languageSettings {
                    kotlinSettings?.apiVersion?.let { apiVersion = it.version }
                    kotlinSettings?.languageVersion?.let { languageVersion = it.version }
                }
            }
        }
        val kotlinBomNotation = "org.jetbrains.kotlin:kotlin-bom:${BuildConstants.KOTLIN_VERSION}"
        val kotlinBom = project.dependencies.platform(kotlinBomNotation)
        project.dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, kotlinBom)
    }

    override fun afterEvaluate() {
        adjustSourceSetLayout()
        project.tasks.withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = kotlinSettings?.jvmTarget ?: toolChainsSettings.languageVersion
                        ?: ToolChainsSettings.DEFAULT_LANGUAGE_VERSION
            }
        }
    }

    private fun adjustSourceSetLayout() {
        project.extensions.configure<SourceSetContainer> {
            named("main") {
                java.setSrcDirs(listOf("src"))
                resources.setSrcDirs(listOf("resources"))
                (extensions.getByName("kotlin") as? SourceDirectorySet)?.setSrcDirs(listOf("src"))
            }
            named("test") {
                java.setSrcDirs(listOf("test"))
                resources.setSrcDirs(listOf("testResources"))
                (extensions.getByName("kotlin") as? SourceDirectorySet)?.setSrcDirs(listOf("test"))
            }
        }
    }

    private val ToolChainsSettings.javaLanguageVersion
        get() = JavaLanguageVersion.of(languageVersion ?: ToolChainsSettings.DEFAULT_LANGUAGE_VERSION)
}
