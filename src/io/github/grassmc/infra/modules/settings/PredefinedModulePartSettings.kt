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

package io.github.grassmc.infra.modules.settings

import io.github.grassmc.infra.modules.ModuleManifestSection

data class PredefinedModulePartSettings(
    val toolchains: ToolChainsSettings? = null,
    val kotlin: KotlinSettings? = null,
    val paper: PaperSettings? = null,
    val test: TestSettings? = null,
) : ModuleManifestSection<PredefinedModulePartSettings> {
    override fun merge(other: PredefinedModulePartSettings) = copy(
        toolchains = other.toolchains?.let { toolchains?.merge(it) ?: it } ?: toolchains,
        kotlin = other.kotlin?.let { kotlin?.merge(it) ?: it } ?: kotlin,
        paper = other.paper?.let { paper?.merge(it) ?: it } ?: paper,
        test = other.test?.let { test?.merge(it) ?: it } ?: test,
    )
}
