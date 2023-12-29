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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.util.StdConverter
import io.github.grassmc.infra.modules.ModuleManifestSection
import org.gradle.jvm.toolchain.JvmVendorSpec

data class ToolChainsSettings(
    val languageVersion: String? = null,
    @JsonDeserialize(converter = StringToJvmVendorSpecConverter::class)
    val vendor: JvmVendorSpec? = null,
) : ModuleManifestSection<ToolChainsSettings> {
    override fun merge(other: ToolChainsSettings) = copy(
        languageVersion = languageVersion ?: other.languageVersion,
        vendor = vendor ?: other.vendor,
    )

    companion object {
        const val DEFAULT_LANGUAGE_VERSION = "17"
        val DEFAULT_VENDOR: JvmVendorSpec = JvmVendorSpec.AZUL
    }
}

private object StringToJvmVendorSpecConverter : StdConverter<String, JvmVendorSpec>() {
    override fun convert(value: String) = JvmVendorSpec.matching(value)
}
