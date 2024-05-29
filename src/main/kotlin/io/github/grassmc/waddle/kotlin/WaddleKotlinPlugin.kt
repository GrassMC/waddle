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

package io.github.grassmc.waddle.kotlin

import io.github.grassmc.waddle.WaddlePlugin
import io.github.grassmc.waddle.configureDefaultRepositories
import io.github.grassmc.waddle.java.WaddleJavaPlugin.Companion.DEFAULT_JDK_VERSION
import io.github.grassmc.waddle.java.WaddleJvmExtension
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * This class represents the wrapper for the `kotlin-jvm` plugin.
 *
 * It configures the target version of the JVM for the project.
 */
@Suppress("unused")
abstract class WaddleKotlinPlugin : WaddlePlugin<Project>() {
    override fun applyPlugins() = listOf(KotlinPluginWrapper::class)

    override fun init(target: Project) {
        target.configureDefaultRepositories()
        target.configureJvmTarget()
    }

    private fun Project.configureJvmTarget() {
        val jdkVersion = extensions.findByType<WaddleJvmExtension>()?.target?.orNull ?: DEFAULT_JDK_VERSION
        project.extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain {
                languageVersion.convention(JavaLanguageVersion.of(jdkVersion))
                vendor.convention(JvmVendorSpec.AZUL)
            }
        }
        project.tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = jdkVersion.toString()
        }
    }
}
