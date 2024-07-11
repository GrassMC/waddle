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
import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.kotlin.dsl.repositories

/**
 * Configures the default repositories for the project.
 *
 * The method checks if the Maven Central repository is already present in the project's repositories.
 * If not, it adds the Maven Central repository to the project.
 */
internal fun Project.configureDefaultRepositories() {
    repositories {
        findByName(ArtifactRepositoryContainer.DEFAULT_MAVEN_CENTRAL_REPO_NAME) ?: mavenCentral()
    }
}

/**
 * Returns the value of a property in the current [Project] or its ancestors, identified by the given [dottedName].
 *
 * This method searches for properties in the current [Project] and its ancestors and returns the value of the first
 * match.
 * The [dottedName] parameter specifies the name of the property to search for.
 * It should be in dot notation, such as "foo.bar".
 *
 * If no property with the exact [dottedName] is found, this method tries two alternative variations:
 * - It replaces all dots (".") in the [dottedName] with underscores ("_") and searches for a match.
 * - It replaces all dots (".") in the [dottedName] with the next character uppercase and searches for a match.
 *
 * If no match is found using any of the variations, this method returns null.
 */
internal fun Project.matchProperty(dottedName: String) =
    findProperty(dottedName)
        ?: findProperty(dottedName.replace('.', '_'))
        ?: findProperty(dottedName.replace(Regex("\\..")) { it.value.substring(1).uppercase() })
