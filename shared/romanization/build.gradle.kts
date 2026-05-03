plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    jvm()

    android {
        namespace = "com.shub39.romanization"
        compileSdk {
            version = release(libs.versions.compileSdk.get().toInt())
        }
    }
}