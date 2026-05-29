@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    android {
        namespace = "com.shub39.rush.shared.ui"
        compileSdk {
            version = release(libs.versions.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.minSdk.get().toInt())
        }
        androidResources { enable = true }
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)

            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.windowsizeclass)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.jetbrains.navigation3.ui)

            implementation(libs.kotlinx.serialization.json)
        }
    }
}