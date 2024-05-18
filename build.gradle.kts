import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    alias(libs.plugins.plugin.publish)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.foojay.resolver)
    implementation(embeddedKotlin("gradle-plugin"))
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
            implementationClass = "io.github.grassmc.waddle.settings.WaddleSettingsPlugin"
        }
        register("waddleJava") {
            id = "io.github.grassmc.waddle-java"
            implementationClass = "io.github.grassmc.waddle.java.WaddleJavaPlugin"
        }
        register("waddleKotlin") {
            id = "io.github.grassmc.waddle-kotlin"
            implementationClass = "io.github.grassmc.waddle.kotlin.WaddleKotlinPlugin"
        }
    }
}
