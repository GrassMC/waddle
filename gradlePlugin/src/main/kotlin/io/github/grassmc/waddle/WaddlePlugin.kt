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

package io.github.grassmc.waddle

import org.gradle.api.Plugin
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.apply
import kotlin.reflect.KClass

/**
 * Represent a plugin that can be applied to an object of type [T].
 *
 * @param T the type of object to apply the plugin to.
 */
abstract class WaddlePlugin<T : PluginAware> : Plugin<T> {
    /**
     * Returns an iterable of classes that represent the plugins to be applied.
     * This method can be overridden by subclasses to provide a different set of plugins.
     */
    protected open fun applyPlugins(): Iterable<KClass<out Plugin<*>>> = emptyList()

    final override fun apply(target: T) {
        applyPlugins().forEach { target.plugins.apply(it) }
        init(target)
    }

    /**
     * Initializes the plugin.
     */
    protected open fun init(target: T) = Unit
}
