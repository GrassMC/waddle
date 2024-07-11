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

package io.github.grassmc.waddle

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.maven

private const val MAVEN_GITHUB_PACKAGES_URL_PREFIX = "https://maven.pkg.github.com/"

/**
 * Adds a Maven artifact repository for accessing packages hosted on GitHub Packages.
 *
 * @param owner The GitHub username or organization name that owns the repository.
 * @param repo The name of the GitHub repository.
 * @param action A lambda function that allows configuring additional settings for the repository.
 * @return The added repository.
 */
@Suppress("unused")
fun RepositoryHandler.githubPackages(
    project: Project,
    owner: String,
    repo: String,
    action: MavenArtifactRepository.() -> Unit = {},
) = maven("$MAVEN_GITHUB_PACKAGES_URL_PREFIX$owner/$repo") {
    configureDefaults(project)
    action()
}

private const val DEFAULT_GITHUB_PACKAGES_REPO_NAME = "githubPackages"
private const val GITHUB_ACTOR_ENV = "GITHUB_ACTOR"
private const val GITHUB_USER_ENV = "GITHUB_USER"
private const val GITHUB_TOKEN_ENV = "GITHUB_TOKEN"
private const val GITHUB_ACTOR_PROP = "github.actor"
private const val GITHUB_USER_PROP = "github.user"
private const val GITHUB_TOKEN_PROP = "github.token"

private fun MavenArtifactRepository.configureDefaults(project: Project) {
    name = DEFAULT_GITHUB_PACKAGES_REPO_NAME
    when {
        System.getenv(GITHUB_TOKEN_ENV) != null -> {
            credentials {
                username = System.getenv(GITHUB_USER_ENV) ?: System.getenv(GITHUB_ACTOR_ENV)
                password = System.getenv(GITHUB_TOKEN_ENV)
            }
        }

        project.matchProperty(GITHUB_TOKEN_PROP) != null -> {
            credentials {
                username =
                    (project.matchProperty(GITHUB_USER_PROP) ?: project.matchProperty(GITHUB_ACTOR_PROP)).toString()
                password = project.matchProperty(GITHUB_TOKEN_PROP).toString()
            }
        }

        else -> credentials(PasswordCredentials::class)
    }
}
