@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.plugin.publish)
    alias(libs.plugins.buildconfig)
}

dependencies {
    implementation(libs.foojay.resolver)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.run.task)
    implementation(libs.paperweight.userdev)
    implementation(libs.shadow)
}

val jdkVersion = 17
kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(jdkVersion)
        vendor = JvmVendorSpec.AZUL
    }
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(jdkVersion.toString())
    }
}

gradlePlugin {
    plugins {
        property("waddle.plugins").toString().trim().split(',').forEach { waddlePlugin ->
            create(waddlePluginName(waddlePlugin)) {
                id = waddlePluginId(waddlePlugin)
                implementationClass = waddlePluginClass(waddlePlugin)
                tags = setOf(waddlePlugin)
            }
        }
    }
}

publishing {
    repositories {
        maven("https://maven.pkg.github.com/GrassMC/waddle") {
            name = "githubPackages"
            credentials(PasswordCredentials::class)
        }
    }
}

mavenPublishing {
    signAllPublications()
    coordinates(artifactId = "waddle-gradle-plugin")
}

buildConfig {
    packageName = "io.github.grassmc.waddle"
    buildConfigField("KOTLIN_VERSION", libs.versions.kotlin)
    buildConfigField("WADDLE_PLUGINS", provider { gradlePlugin.plugins.associate { it.tags.get().first() to it.id } })
}

fun waddlePluginName(waddlePlugin: String) =
    buildString {
        append("waddle")
        for (part in waddlePlugin.split('.')) {
            append(part.replaceFirstChar { it.uppercase() })
        }
    }

fun waddlePluginId(waddlePlugin: String) =
    "io.github.grassmc.waddle${if (waddlePlugin == "settings") "" else "-$waddlePlugin"}"

fun waddlePluginClass(waddlePlugin: String) =
    buildString {
        append("io.github.grassmc.waddle.")
        val parts = waddlePlugin.split('.')
        append(parts.first())
        append(".Waddle")
        for (part in parts) {
            append(part.replaceFirstChar { it.uppercase() })
        }
        append("Plugin")
    }
