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
import io.github.kdroidfilter.nucleus.desktop.application.dsl.AppImageCategory
import io.github.kdroidfilter.nucleus.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.nucleus)
}

kotlin {
    compilerOptions {
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        optIn.add(
            "androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi"
        )
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.ui)
            implementation(projects.shared.logic)

            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.windowsizeclass)

            implementation(libs.jetbrains.navigation3.ui)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.koin.annotations)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

val appVersion = execute("awk", "/^## / {sub(/^## /, \"\"); print; exit}", "CHANGELOG.md")

nucleus.application {
    mainClass = "com.shub39.rush.MainKt"
    jvmArgs += listOf("-Xmx512m")

    buildTypes { release { proguard { isEnabled = false } } }

    nativeDistributions {
        targetFormats(TargetFormat.AppImage)

        appName = "Rush"
        packageName = "Rush"
        packageVersion = appVersion
        description = "Save, Search and Share Lyrics like Spotify!"
        vendor = "shub39"
        copyright = "Copyright 2025 Shubham Gorai"
        licenseFile.set(project.file("LICENSE"))

        modules("java.sql", "java.net.http", "jdk.unsupported")

        cleanupNativeLibs = true
        artifactName = $$"${name}-${version}-${os}-${arch}.${ext}"

        linux {
            iconFile.set(rootProject.file("fastlane/metadata/android/en-US/images/icon.png"))

            shortcut = true
            packageName = "rush"
            appRelease = appVersion
            appCategory = "Utility"
            menuGroup = "Development"

            appImage {
                category = AppImageCategory.Utility
                genericName = "Rush"
                synopsis = "Save, Search and Share Lyrics like Spotify!"
            }
        }
    }
}

fun execute(vararg command: String): String =
    providers.exec { commandLine(*command) }.standardOutput.asText.get().trim()
