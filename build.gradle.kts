plugins {
    `kotlin-dsl`
    signing
    alias(libs.plugins.com.gradle.plugin.publish)
}

dependencies {
    implementation(libs.foojay.resolver)
    implementation(libs.kotlin.gradle.plugin)

    implementation(platform(libs.jackson.bom))
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.dataformat.yaml)

    implementation(libs.asm)

    implementation(libs.paperweight.userdev)
    implementation(libs.run.task)
}

val generatedBuildConstants = layout.buildDirectory.dir("generated/sources/buildConstants")
sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
        (extensions.getByName("kotlin") as SourceDirectorySet).setSrcDirs(listOf("src", generatedBuildConstants))
    }
    test {
        java.setSrcDirs(listOf("test"))
        resources.setSrcDirs(listOf("testResources"))
        (extensions.getByName("kotlin") as SourceDirectorySet).setSrcDirs(listOf("test"))
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.AZUL
    }
}

gradlePlugin {
    plugins {
        val infra by creating {
            id = "io.github.grassmc.infra"
            implementationClass = "io.github.grassmc.infra.plugins.InfraSettingsPlugin"
        }
    }
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey?.replace("\\n", "\n"), signingPassword)
}

publishing {
    repositories {
        maven {
            name = "GrassMCRepo"
            url = uri("s3://grassmc-repo")
            credentials(AwsCredentials::class) {
                accessKey = System.getenv("S3_ACCESS_KEY")
                secretKey = System.getenv("S3_SECRET_KEY")
            }
        }
    }
}

tasks {
    val generateBuildConstants by registering {
        doLast {
            generatedBuildConstants.get().asFile.mkdirs()
            val buildConstants = generatedBuildConstants.get().asFile.resolve("BuildConstants.kt")
            buildConstants.writeText(
                """
                internal object BuildConstants {
                    const val KOTLIN_VERSION = "${libs.versions.kotlin.get()}"
                }
                """.trimIndent()
            )
        }
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        dependsOn(generateBuildConstants)
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    validatePlugins {
        enableStricterValidation = true
    }
}
