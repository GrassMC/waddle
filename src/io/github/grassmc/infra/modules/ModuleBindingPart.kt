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

package io.github.grassmc.infra.modules

import org.gradle.api.Project

interface ModuleBindingPart {
    val project: Project
    val manifest: ModuleManifest

    val needToApply get() = false

    fun beforeEvaluate() = Unit

    fun afterEvaluate() = Unit
}

data class BindingPartCtx(override val project: Project, override val manifest: ModuleManifest) : ModuleBindingPart

internal val ModuleBindingPart.settings get() = requireNotNull(manifest.settings) { "Module settings is null" }
