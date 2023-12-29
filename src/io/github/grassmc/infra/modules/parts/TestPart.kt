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

package io.github.grassmc.infra.modules.parts

import io.github.grassmc.infra.modules.BindingPartCtx
import io.github.grassmc.infra.modules.ModuleBindingPart
import io.github.grassmc.infra.modules.settings
import org.gradle.api.plugins.JavaPlugin

class TestPart(ctx: BindingPartCtx) : ModuleBindingPart by ctx {
    private val testSettings get() = settings.test

    override val needToApply: Boolean get() = testSettings?.kotlin == true

    override fun beforeEvaluate() {
        if (testSettings?.kotlin == true) {
            project.dependencies.add(
                JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,
                "org.jetbrains.kotlin:kotlin-test-junit5"
            )
        }
        if (testSettings?.mockk == true) {
            project.dependencies.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "io.mockk:mockk:1.13.8")
        }
    }
}
