/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.koin.compiler)
}

val appName = "Rush"
val appVersionName = "6.3.2"
val appVersionCode = 6320

val gitHash = execute("git", "rev-parse", "HEAD").take(7)

android {
    namespace = "com.shub39.rush"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.shub39.rush"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
        androidResources { generateLocaleConfig = true }
    }

    buildTypes {
        release {
            resValue("string", "app_name", appName)
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        create("beta") {
            resValue("string", "app_name", "$appName Beta")
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta$gitHash"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "$appName Debug")
            versionNameSuffix = "-debug"
        }
    }

    flavorDimensions += "version"

    productFlavors {
        create("play") {
            dimension = "version"
            applicationIdSuffix = ".play"
            versionNameSuffix = "-play"
        }
        create("foss") { dimension = "version" }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/CONTRIBUTORS.md"
            excludes += "META-INF/LICENSE.md"
        }
        jniLibs.keepDebugSymbols.add("**/*.so")
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    sourceSets {
        getByName("main") {
            res.directories.addAll(
                listOf(
                    "src/main/res",
                    "${project(":shared:ui").projectDir}/src/commonMain/composeResources",
                )
            )
        }
    }
}

kotlin {
    compilerOptions {
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        optIn.add(
            "androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi"
        )
    }
}

dependencies {
    implementation(projects.shared.core)
    implementation(projects.shared.ui)
    implementation(projects.shared.logic)

    implementation(projects.androidLibs.visualizerHelper)
    implementation(projects.androidLibs.romanization)

    "playImplementation"(libs.purchases.ui)
    "playImplementation"(libs.purchases)

    implementation(libs.compose.material3)
    implementation(libs.compose.components.resources)
    implementation(libs.compose.windowsizeclass)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.jetbrains.navigation3.ui)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.koin.annotations)

    implementation(libs.landscapist.coil)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
}

fun execute(vararg command: String): String =
    providers.exec { commandLine(*command) }.standardOutput.asText.get().trim()
