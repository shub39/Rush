plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.ksp)
}

val appName = "Rush"

android {
    namespace = "com.shub39.rush"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shub39.rush"
        minSdk = 29
        targetSdk = 35
        versionCode = 3010
        versionName = "3.0.1"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
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

aboutLibraries {
    // Remove the "generated" timestamp to allow for reproducible builds; from kaajjo/LibreSudoku
    excludeFields = arrayOf("generated")
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.datetime)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.palette)
    implementation(libs.koin.androidx.compose)
    implementation(libs.jetbrains.compose.navigation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.placeholder)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.room.compiler)
    ksp(libs.androidx.room.room.compiler)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.sqlite.bundled)
    implementation(libs.colorpicker.compose)
    implementation(libs.ksoup)
    implementation(libs.materialKolor)
    implementation(libs.bundles.ktor)
    implementation(libs.hypnoticcanvas)
    implementation(libs.aboutLibraries)
    implementation(libs.composeIcons.fontAwesome)
    testImplementation(libs.junit)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}