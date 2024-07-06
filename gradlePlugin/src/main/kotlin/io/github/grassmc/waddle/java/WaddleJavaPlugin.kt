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

package io.github.grassmc.waddle.java

import io.github.grassmc.waddle.WaddlePlugin
import io.github.grassmc.waddle.configureDefaultRepositories
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType

/**
 * This class represents the wrapper for the `java` plugin.
 *
 * It configures the target version of the JVM for the project.
 */
abstract class WaddleJavaPlugin : WaddlePlugin<Project>() {
    override fun applyPlugins() = listOf(JavaPlugin::class)

    override fun init(target: Project) {
        target.configureDefaultRepositories()
        val waddleJvm = target.getOrCreateJvmExtension()
        target.configureJavaTarget(waddleJvm.target.get())
    }

    private fun Project.configureJavaTarget(version: Int) {
        extensions.configure<JavaPluginExtension> {
            val javaVersion = JavaVersion.toVersion(version)
            targetCompatibility = javaVersion
            sourceCompatibility = javaVersion
            toolchain {
                languageVersion.convention(JavaLanguageVersion.of(version))
                vendor.convention(JvmVendorSpec.AZUL)
            }
        }

        tasks.withType<JavaCompile> {
            options.release.convention(version)
        }
    }

    companion object {
        private const val WADDLE_JVM_EXTENSION_NAME = "jvm"
        internal const val DEFAULT_JDK_VERSION = 21

        internal fun Project.getOrCreateJvmExtension() =
            extensions.findByType<WaddleJvmExtension>() ?: createWaddleJvmExtension()

        private fun Project.createWaddleJvmExtension() =
            extensions.create<WaddleJvmExtension>(WADDLE_JVM_EXTENSION_NAME).apply {
                target.convention(DEFAULT_JDK_VERSION)
            }
    }
}
