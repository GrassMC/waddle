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

import com.fasterxml.jackson.annotation.JsonValue
import io.github.grassmc.infra.modules.ModuleManifestSection

data class PaperSettings(
    val version: PaperVersion? = null,
    val internal: Boolean? = null,
    val acceptEula: Boolean? = null,
) : ModuleManifestSection<PaperSettings> {
    override fun merge(other: PaperSettings) = copy(
        version = version ?: other.version,
        internal = internal ?: other.internal,
        acceptEula = acceptEula ?: other.acceptEula,
    )
}

@Suppress("unused")
enum class PaperVersion(@JsonValue val minecraftVersion: String) {
    PAPER_1_19_3("1.19.3"),
    PAPER_1_19_4("1.19.4"),
    PAPER_1_20("1.20"),
    PAPER_1_20_1("1.20.1"),
    PAPER_1_20_2("1.20.2"),
    PAPER_1_20_4("1.20.4");

    val artifactVersion = "$minecraftVersion-R0.1-SNAPSHOT"

    companion object {
        @JvmStatic
        val DEFAULT = PAPER_1_20_4
    }
}
