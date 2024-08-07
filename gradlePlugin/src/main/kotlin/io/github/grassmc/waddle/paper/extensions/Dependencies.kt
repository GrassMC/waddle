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

@file:Suppress("UnusedReceiverParameter")

package io.github.grassmc.waddle.paper.extensions

import io.github.grassmc.waddle.DEFAULT_MINECRAFT_VERSION
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.paperApi(minecraftVersion: String = DEFAULT_MINECRAFT_VERSION) =
    "io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT"
