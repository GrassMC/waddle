# infra-gradle

🧰 A Gradle plugin that helps us to manage GrassMC projects

## Usage

### `settings.gradle.kts`

```kotlin
dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/GrassMC/infra-gradle")
            credentials {
                username = providers.gradleProperty("github.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("github.token").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

plugins {
    id("io.github.grassmc.infra") version "0.1.0"
}
```

## License

This project is licensed under the Apache License 2.0, see the [LICENSE](LICENSE) file for details.
