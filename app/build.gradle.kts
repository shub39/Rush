import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
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
    alias(libs.plugins.buildKonfig)
}

val appName = "Rush"
val appVersionName = "3.6.0"
val appVersionCode = 3600

buildkonfig {
    packageName = "com.shub39.rush"
    objectName = "BuildKonfig"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(STRING, "versionName", appVersionName)
        buildConfigField(STRING, "versionCode", appVersionCode.toString())
    }
}

android {
    namespace = "com.shub39.rush"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.shub39.rush"
        minSdk = 29
        targetSdk = 36
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

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.android)
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
            implementation(libs.datetime)
            implementation(libs.bundles.ktor)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.materialKolor)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.landscapist.coil)
            implementation(libs.landscapist.placeholder)
            implementation(libs.colorpicker.compose)
            implementation(libs.ksoup)
            implementation(libs.hypnoticcanvas)
            implementation(libs.aboutLibraries)
            implementation(libs.aboutLibraries.compose.m3)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)
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
    export.excludeFields.add("generated")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}