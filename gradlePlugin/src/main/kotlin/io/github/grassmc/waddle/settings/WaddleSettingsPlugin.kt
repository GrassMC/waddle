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
                waddleProjectPluginIdsByAlias().forEach { (alias, id) ->
                    plugin(alias, id).version {}
                }
            }
        }
    }

    private fun waddleProjectPluginIdsByAlias() =
        findWaddleProjectPlugins().associateWith { "$WADDLE_PROJECT_PLUGIN_PREFIX$it" }

    private fun findWaddleProjectPlugins() =
        WaddleSettingsPlugin::class.java.classLoader
            .getResourceAsStream(WADDLE_PLUGINS_CLASSPATH)!!
            .bufferedReader()
            .readLine()
            .trim()
            .split(',')
            .filterNot { it.endsWith(SETTINGS_PLUGIN) }

    companion object {
        const val MINIMUM_GRADLE_VERSION = "8.8"
        private const val WADDLE_VERSIONS_CATALOG_NAME = "waddle"

        private const val WADDLE_PROJECT_PLUGIN_PREFIX = "io.github.grassmc.waddle-"
        private const val WADDLE_PLUGINS_CLASSPATH = "waddle-plugins"
        private const val SETTINGS_PLUGIN = "settings"
    }
}
