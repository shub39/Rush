import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.ksp)
}

val appName = "Rush"
val appVersionName = "4.1.2"
val appVersionCode = 4120

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

    kotlin {
        compilerOptions {
            jvmToolchain(17)
        }
    }

    buildTypes {
        release {
            resValue("string", "app_name", appName)
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
//            applicationIdSuffix = ".debug"
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
        create("foss") {
            dimension = "version"
        }
    }

    applicationVariants.all {
        outputs.all {
            val apkOutput = this as ApkVariantOutputImpl
            apkOutput.outputFileName = "app-release.apk"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

dependencies {
    "playImplementation"(libs.purchases.ui)
    "playImplementation"(libs.purchases)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kmpalette.core)
    implementation(libs.androidx.material.icons.extended)
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
    implementation(libs.filekit.core)
    implementation(libs.filekit.dialogs.compose)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    ksp(libs.koin.ksp.compiler)
    api(libs.koin.annotations)
}

ksp {
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_DEFAULT_MODULE", "true")
}

aboutLibraries {
    export.excludeFields.add("generated")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}