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

import org.gradle.api.provider.Property

/**
 * This interface represents the WaddleJvmExtension.
 *
 * It provides a property for the [target] version of the JVM.
 */
interface WaddleJvmExtension {
    /**
     * The target version of the JVM.
     *
     * `Int` value of the target version of the JVM (e.g. 8 (Java 8), 11, 16).
     * This property is used to set the target version of the JVM-related tasks and configurations.
     */
    val target: Property<Int>
}
