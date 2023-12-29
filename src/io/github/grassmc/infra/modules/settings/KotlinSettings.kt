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
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

data class KotlinSettings(
    val jvmTarget: String? = null,
    val explicitApi: ExplicitApiMode? = null,
    @JsonDeserialize(converter = StringToKotlinVersionConverter::class)
    val apiVersion: KotlinVersion? = null,
    @JsonDeserialize(converter = StringToKotlinVersionConverter::class)
    val languageVersion: KotlinVersion? = null,
) : ModuleManifestSection<KotlinSettings> {
    override fun merge(other: KotlinSettings) = copy(
        jvmTarget = jvmTarget ?: other.jvmTarget,
        explicitApi = explicitApi ?: other.explicitApi,
        apiVersion = apiVersion ?: other.apiVersion,
        languageVersion = languageVersion ?: other.languageVersion,
    )

    companion object {
        val DEFAULT_EXPLICIT_API = ExplicitApiMode.Disabled
    }
}

private object StringToKotlinVersionConverter : StdConverter<String, KotlinVersion>() {
    override fun convert(value: String) = KotlinVersion.fromVersion(value)
}
