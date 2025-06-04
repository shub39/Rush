import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.ComposeHotRun
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.ksp)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.room)
    alias(libs.plugins.hotreload)
}

val appName = "Rush"
val appVersionName = "3.3.0"
val appVersionCode = 3300

android {
    namespace = "com.shub39.rush"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shub39.rush"
        minSdk = 29
        targetSdk = 35
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        // Fdroid, github and IzzyOnDroid
        release {
            resValue("string", "app_name", appName)
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        // Testing
        debug {
            resValue("string", "app_name", "$appName Debug")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }

        // Beta
        create("beta"){
            resValue("string", "app_name", "$appName Beta")
            applicationIdSuffix = ".beta"
            isMinifyEnabled = true
            isShrinkResources = true
            versionNameSuffix = "-beta"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        // Playstore
        create("play") {
            resValue("string", "app_name", appName)
            applicationIdSuffix = ".play"
            isMinifyEnabled = true
            isShrinkResources = true
            versionNameSuffix = "-playstore"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

kotlin {
    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    jvm("desktop")

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.android)
            implementation(libs.androidx.documentfile)
        }
        commonMain.dependencies {
            implementation(libs.kmpalette.core)
            implementation(libs.material.icons.core)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.room.runtime)
            implementation(libs.composeIcons.fontAwesome)
            implementation(libs.zoomable)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.sqlite.bundled)
            implementation(libs.datetime)
            implementation(libs.bundles.ktor)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.materialKolor)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.landscapist.coil)
            implementation(libs.landscapist.placeholder)
            implementation(libs.colorpicker.compose)
            implementation(libs.ksoup)
            implementation(libs.hypnoticcanvas)
            implementation(libs.aboutLibraries)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        dependencies {
            debugImplementation(compose.uiTooling)
            annotationProcessor(libs.androidx.room.room.compiler)
            ksp(libs.androidx.room.room.compiler)
            testImplementation(libs.junit)
        }
    }
}

aboutLibraries {
    // Remove the "generated" timestamp to allow for reproducible builds; from kaajjo/LibreSudoku
    export.excludeFields.add("generated")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

compose.desktop {
    application {
        mainClass = "com.shub39.rush.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Rpm, TargetFormat.Deb, TargetFormat.AppImage)
            packageName = appName
            packageVersion = appVersionName
            licenseFile.set(project.file("../LICENSE"))

            linux {
                rpmLicenseType = "GPLv3"
                shortcut = true

                iconFile.set(project.file("../fastlane/metadata/android/en-US/images/icon.png"))
            }

            jvmArgs("-Dcompose.application.configure.swing.globals=false")

            buildTypes.release.proguard {
                isEnabled.set(false)
                obfuscate.set(false)
                optimize.set(true)
                configurationFiles.from("proguard-rules.pro")
            }
        }
    }
}

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}

tasks.register<ComposeHotRun>("runHot") {
    mainClass.set("com.shub39.rush.MainKt")
}