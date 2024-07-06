plugins {
    `kotlin-dsl` apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.plugin.publish) apply false
    alias(libs.plugins.buildconfig) apply false
}
