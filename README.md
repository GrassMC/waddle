# infra-gradle

🧰 A Gradle plugin that helps us to manage GrassMC projects

## Usage

<!-- x-release-please-start-version -->

1. Apply the plugin in your `settings.gradle.kts` file.

    ```kotlin
    // settings.gradle.kts
    
    pluginManagement {
        repositories {
            gradlePluginPortal()
            maven("https://maven.nguyenthanhtan.id.vn/releases")
        }
    }
    
    plugins {
        id("io.github.grassmc.waddle") version "1.0.0-SNAPSHOT"
    }
    ```

2. Apply theses following plugin based on project type (apply to `build.gradle.kts`).

    - `io.github.grassmc.waddle-java`: Wrapper for Java plugin.
    - `io.github.grassmc.waddle-kotlin`: Wrapper for Kotlin JVM plugin.
    - `io.github.grassmc.waddle-paper`: Plugin for Paper plugin development.
    - `io.github.grassmc.waddle-paper.internal`: Plugin for Paper plugin development with internal dependencies (knows
      as NMS).
    - `io.github.grassmc.waddle-shadow`: Conventions for Shadow plugin.

<!-- x-release-please-end -->

## License

This project is licensed under the Apache License 2.0, see the [LICENSE](LICENSE) file for details.
