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

import io.github.grassmc.waddle.DEFAULT_KOTLINX_ATOMICFU_VERSION
import io.github.grassmc.waddle.DEFAULT_KOTLINX_COLLECTIONS_IMMUTABLE_VERSION
import io.github.grassmc.waddle.DEFAULT_KOTLINX_COROUTINES_VERSION
import io.github.grassmc.waddle.DEFAULT_KOTLINX_DATETIME_VERSION
import io.github.grassmc.waddle.DEFAULT_KOTLINX_IO_VERSION
import io.github.grassmc.waddle.DEFAULT_KOTLINX_SERIALIZATION_VERSION
import io.github.grassmc.waddle.DEFAULT_KOTLIN_VERSION
import io.github.grassmc.waddle.DEFAULT_MINECRAFT_VERSION
import io.github.grassmc.waddle.WADDLE_PLUGINS
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.gradle.api.initialization.resolve.MutableVersionCatalogContainer

internal object WaddleVersionCatalogCreator {
    private const val WADDLE_VERSIONS_CATALOG_NAME = "waddle"
    private const val SETTINGS_PLUGIN = "settings"

    fun MutableVersionCatalogContainer.create() {
        create(WADDLE_VERSIONS_CATALOG_NAME) {
            version("minecraft", DEFAULT_MINECRAFT_VERSION)
            WADDLE_PLUGINS.filterNot { it.key == SETTINGS_PLUGIN }.forEach { (alias, id) ->
                plugin(alias, id).version {}
            }
            kotlinLibs()
            kotlinxLibs()
        }
    }

    private fun VersionCatalogBuilder.kotlinLibs() {
        version(Alias.KOTLIN, DEFAULT_KOTLIN_VERSION)
        kotlin(Alias.BOM).versionRef(Alias.KOTLIN)
        kotlin("stdlib").withoutVersion()
        kotlin("reflect").withoutVersion()
        kotlin(Alias.TEST).withoutVersion()
    }

    private fun VersionCatalogBuilder.kotlin(module: String) =
        library("kotlin-$module", Groups.KOTLIN, "kotlin-$module")

    private fun VersionCatalogBuilder.kotlinxLibs() {
        kotlinxCoroutinesLibs()
        kotlinxSerializationLibs()
        kotlinxDatetimeLibs()
        kotlinxAtomicFuLibs()
        kotlinxIoLibs()
        kotlinxCollectionsImmutableLibs()
    }

    private fun VersionCatalogBuilder.kotlinxCollectionsImmutableLibs() {
        version(Alias.KOTLINX_COLLECTIONS_IMMUTABLE, DEFAULT_KOTLINX_COLLECTIONS_IMMUTABLE_VERSION)
        kotlinx("collections-immutable").versionRef(Alias.KOTLINX_COLLECTIONS_IMMUTABLE)
    }

    private fun VersionCatalogBuilder.kotlinxIoLibs() {
        version(Alias.KOTLINX_IO, DEFAULT_KOTLINX_IO_VERSION)
        kotlinxIo(Alias.CORE).versionRef(Alias.KOTLINX_IO)
        kotlinxIo("bytestring").versionRef(Alias.KOTLINX_IO)
    }

    private fun VersionCatalogBuilder.kotlinxIo(module: String) = kotlinx("io-$module")

    private fun VersionCatalogBuilder.kotlinxAtomicFuLibs() {
        version(Alias.KOTLINX_ATOMICFU, DEFAULT_KOTLINX_ATOMICFU_VERSION)
        plugin(Alias.KOTLINX_ATOMICFU, "${Groups.KOTLINX}.atomicfu").versionRef(Alias.KOTLINX_ATOMICFU)
    }

    private fun VersionCatalogBuilder.kotlinxDatetimeLibs() {
        version(Alias.KOTLINX_DATETIME, DEFAULT_KOTLINX_DATETIME_VERSION)
        kotlinx("datetime").versionRef(Alias.KOTLINX_DATETIME)
    }

    private fun VersionCatalogBuilder.kotlinxCoroutinesLibs() {
        version(Alias.KOTLINX_COROUTINES, DEFAULT_KOTLINX_COROUTINES_VERSION)
        kotlinxCoroutines(Alias.BOM).versionRef(Alias.KOTLINX_COROUTINES)
        kotlinxCoroutines(Alias.CORE).withoutVersion()
        kotlinxCoroutines(Alias.TEST).withoutVersion()
        kotlinxCoroutines("debug").withoutVersion()
    }

    private fun VersionCatalogBuilder.kotlinxCoroutines(module: String) = kotlinx("coroutines-$module")

    private fun VersionCatalogBuilder.kotlinxSerializationLibs() {
        plugin("kotlin-plugin-serialization", "${Groups.KOTLIN}.plugin.serialization").versionRef(Alias.KOTLIN)
        version(Alias.KOTLINX_SERIALIZATION, DEFAULT_KOTLINX_SERIALIZATION_VERSION)
        kotlinxSerialization(Alias.BOM).versionRef(Alias.KOTLINX_SERIALIZATION)
        kotlinxSerialization("json").withoutVersion()
    }

    private fun VersionCatalogBuilder.kotlinxSerialization(module: String) = kotlinx("serialization-$module")

    private fun VersionCatalogBuilder.kotlinx(module: String) =
        library("kotlinx-$module", Groups.KOTLINX, "kotlinx-$module")

    private object Alias {
        const val KOTLIN = "kotlin"
        const val KOTLINX_COROUTINES = "kotlinx-coroutines"
        const val KOTLINX_SERIALIZATION = "kotlinx-serialization"
        const val KOTLINX_DATETIME = "kotlinx-datetime"
        const val KOTLINX_ATOMICFU = "kotlinx-atomicfu"
        const val KOTLINX_IO = "kotlinx-io"
        const val KOTLINX_COLLECTIONS_IMMUTABLE = "kotlinx-collections-immutable"

        const val BOM = "bom"
        const val CORE = "core"
        const val TEST = "test"
    }

    private object Groups {
        const val KOTLIN = "org.jetbrains.kotlin"
        const val KOTLINX = "org.jetbrains.kotlinx"
    }
}
