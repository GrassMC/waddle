import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

val jdkVersion = 17
kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(jdkVersion)
        vendor = JvmVendorSpec.AZUL
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = jdkVersion.toString()
        }
    }
}

gradlePlugin {
    plugins {
        register("waddleSettings") {
            id = "io.github.grassmc.waddle"
            implementationClass = "io.github.grassmc.waddle.plugins.WaddleSettingsPlugin"
        }
    }
}
