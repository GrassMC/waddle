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

package io.github.grassmc.waddle.settings

import io.github.grassmc.waddle.DEFAULT_KOTLIN_VERSION
import io.github.grassmc.waddle.DEFAULT_MINECRAFT_VERSION
import io.github.grassmc.waddle.WADDLE_PLUGINS
import io.github.grassmc.waddle.WaddlePlugin
import org.gradle.api.initialization.Settings
import org.gradle.toolchains.foojay.FoojayToolchainsConventionPlugin
import org.gradle.util.GradleVersion

/**
 * Base [Settings] plugin for waddle plugins.
 *
 * By default, this plugin applies the [FoojayToolchainsConventionPlugin] to configure the toolchains for the project.
 */
@Suppress("unused")
abstract class WaddleSettingsPlugin : WaddlePlugin<Settings>() {
    override fun applyPlugins() = listOf(FoojayToolchainsConventionPlugin::class)

    override fun init(target: Settings) {
        checkGradleVersion()
        target.createWaddleVersionsCatalog()
    }

    private fun checkGradleVersion() {
        if (GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
            throw IllegalStateException("Requires at least Gradle $MINIMUM_GRADLE_VERSION")
        }
    }

    private fun Settings.createWaddleVersionsCatalog() {
        dependencyResolutionManagement.versionCatalogs {
            create(WADDLE_VERSIONS_CATALOG_NAME) {
                version("kotlin", DEFAULT_KOTLIN_VERSION)
                version("minecraft", DEFAULT_MINECRAFT_VERSION)
                WADDLE_PLUGINS.filterNot { it.key == SETTINGS_PLUGIN }.forEach { (alias, id) ->
                    plugin(alias, id).version {}
                }
            }
        }
    }

    companion object {
        const val MINIMUM_GRADLE_VERSION = "8.8"
        private const val WADDLE_VERSIONS_CATALOG_NAME = "waddle"

        private const val SETTINGS_PLUGIN = "settings"
    }
}
