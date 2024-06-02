import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.plugin.publish)
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
        register("waddlePaper") {
            id = "io.github.grassmc.waddle-paper"
            implementationClass = "io.github.grassmc.waddle.paper.WaddlePaperPlugin"
        }
        register("waddlePaperInternal") {
            id = "io.github.grassmc.waddle-paper.internal"
            implementationClass = "io.github.grassmc.waddle.paper.WaddlePaperInternalPlugin"
        }
        register("waddleShadow") {
            id = "io.github.grassmc.waddle-shadow"
            implementationClass = "io.github.grassmc.waddle.shadow.WaddleShadowPlugin"
        }
    }
}

publishing {
    repositories {
        maven("https://maven.nguyenthanhtan.id.vn/releases") {
            name = "tozydevReleases"
            credentials(PasswordCredentials::class)
        }
    }
}

mavenPublishing {
    signAllPublications()
}

tasks {
    val writeWaddlePluginsProperty by registering

    jar {
        from(writeWaddlePluginsProperty)
    }

    afterEvaluate {
        writeWaddlePluginsProperty.configure {
            val waddlePluginsFile = temporaryDir.resolve("waddle-plugins")
            waddlePluginsFile.writeText(project.property("waddle.plugins").toString())
            outputs.file(waddlePluginsFile)
        }
    }
}
