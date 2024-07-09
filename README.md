# waddle

[![GitHub Release](https://img.shields.io/github/v/release/GrassMC/waddle)](https://github.com/GrassMC/waddle/releases/)
[![GitHub deployments](https://img.shields.io/github/deployments/GrassMC/waddle/GitHub%20Packages?label=publish)](https://maven.nguyenthanhtan.id.vn/)

🧰 A Gradle plugin that helps us to manage GrassMC projects

## Usage

<!-- x-release-please-released-start-version -->

1. Apply the plugin in your `settings.gradle.kts` file.

    ```kotlin
    // settings.gradle.kts
    
    pluginManagement {
        repositories {
            gradlePluginPortal()
            maven("https://maven.pkg.github.com/GrassMC/waddle")
        }
    }
    
    plugins {
        id("io.github.grassmc.waddle") version "2.3.0"
    }
    ```

2. Apply theses following plugin based on project type (apply to `build.gradle.kts`).

    - `io.github.grassmc.waddle-java`: Wrapper for Java plugin.
    - `io.github.grassmc.waddle-kotlin`: Wrapper for Kotlin JVM plugin.
    - `io.github.grassmc.waddle-paper`: Plugin for Paper plugin development.
    - `io.github.grassmc.waddle-paper.internal`: Plugin for Paper plugin development with internal dependencies (knows
      as NMS).
    - `io.github.grassmc.waddle-shadow`: Conventions for Shadow plugin.

<!-- x-release-please-released-end -->

## License

This project is licensed under the Apache License 2.0, see the [LICENSE](LICENSE) file for details.
