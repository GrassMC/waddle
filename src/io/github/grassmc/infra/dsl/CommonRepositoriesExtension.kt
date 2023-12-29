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

@file:Suppress("unused")

package io.github.grassmc.infra.dsl

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven
import javax.inject.Inject

abstract class CommonRepositoriesExtension @Inject constructor(private val project: Project) {
    private val repositories = project.repositories

    fun jitpack(action: MavenArtifactRepository.() -> Unit = {}) = repositories.maven("https://jitpack.io", action)

    fun sonatypeSnapshots(action: MavenArtifactRepository.() -> Unit = {}) =
        repositories.maven("https://oss.sonatype.org/content/repositories/snapshots/", action)

    fun s01SonatypeSnapshots(action: MavenArtifactRepository.() -> Unit = {}) =
        repositories.maven("https://s01.oss.sonatype.org/content/repositories/snapshots/", action)

    fun kotlinDev(action: MavenArtifactRepository.() -> Unit = {}) =
        repositories.maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/", action)

    fun githubPackages(owner: String, repo: String, action: MavenArtifactRepository.() -> Unit = {}) =
        repositories.maven("https://maven.pkg.github.com/$owner/$repo") {
            credentials {
                username = project.findProperty("github.user") as? String ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("github.token") as? String ?: System.getenv("GITHUB_TOKEN")
            }
            action()
        }
}
