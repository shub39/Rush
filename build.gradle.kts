plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21"
}

buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}