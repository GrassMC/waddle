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

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.grassmc.infra.modules.settings.PredefinedModulePartSettings
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.name

private val yamlMapper by lazy {
    YAMLMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.KEBAB_CASE
        registerKotlinModule()
    }
}

private const val GRASS_MODULE_YAML = "grass-module.yaml"

val Path.isGrassModuleYaml get() = name == GRASS_MODULE_YAML

internal val Path.grassModuleYaml: Path get() = resolve(GRASS_MODULE_YAML)

internal fun Path.readModuleName() = bufferedReader().use { yamlMapper.readTree(it) }["name"]?.asText()

internal fun Path.readCandidateModuleManifest(): CandidateModuleManifest? =
    bufferedReader().use { yamlMapper.readValue<CandidateModuleManifest>(it) }

internal fun Path.readModuleManifest(): GrassModuleManifest {
    var candidateManifest = requireNotNull(readCandidateModuleManifest()) {
        "Cannot read module manifest from $this"
    }
    while (candidateManifest.from != null) {
        candidateManifest = parseModuleManifest(parent, candidateManifest)
    }

    requireNotNull(candidateManifest.type) { "Module type is not defined" }
    return GrassModuleManifest(
        type = candidateManifest.type!!,
        settings = candidateManifest.settings ?: PredefinedModulePartSettings(),
    )
}


private fun parseModuleManifest(workingDir: Path, current: CandidateModuleManifest): CandidateModuleManifest {
    current.from ?: return current
    val parent = workingDir.resolve(current.from).readCandidateModuleManifest()
    requireNotNull(parent) { "Cannot read parent module manifest from ${current.from}" }
    return current.copy(
        from = parent.from,
        type = current.type ?: parent.type,
        settings = parent.settings?.let { current.settings?.merge(it) ?: it } ?: current.settings,
    )
}
