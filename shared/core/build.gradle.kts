@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    android {
        namespace = "com.shub39.rush.shared.core"
        compileSdk {
            version = release(libs.versions.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.minSdk.get().toInt())
        }
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    jvm()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        commonMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}