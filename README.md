# infra-gradle

🧰 A Gradle plugin that helps us to manage GrassMC projects

## Usage

### `settings.gradle.kts`

```kotlin
dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        maven("https://grassmc-repo.s3.ap-southeast-1.amazonaws.com/")
    }
}

plugins {
    id("io.github.grassmc.infra") version "0.1.0"
}
```

## License

This project is licensed under the Apache License 2.0, see the [LICENSE](LICENSE) file for details.
