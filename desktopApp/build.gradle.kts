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
import io.github.kdroidfilter.nucleus.desktop.application.tasks.AbstractJPackageTask
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

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
            implementation(libs.jetbrains.navigation3.adaptive)

            implementation(libs.jetbrains.navigation3.ui)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.koin.annotations)
        }

        jvmMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

val appVersion =
    execute(
        "awk",
        "/^## / {sub(/^## /, \"\"); print; exit}",
        rootProject.file("CHANGELOG.md").absolutePath,
    )

nucleus.application {
    mainClass = "com.shub39.rush.MainKt"
    jvmArgs += listOf("-Xmx512m")

    val envJavaHome = System.getenv("JAVA_HOME")
    if (envJavaHome != null && File(envJavaHome, "bin/jpackage").exists()) {
        javaHome = envJavaHome
    }

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

        modules(
            "java.sql",
            "java.net.http",
            "java.desktop",
            "java.logging",
            "java.naming",
            "java.management",
            "java.xml",
            "jdk.unsupported",
        )

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

// Nucleus <= 2.5.x sets -Dskiko.library.path=$APPDIR in Rush.cfg, but only unpacks per-platform
// skiko-awt-runtime-<os>-<arch> JARs and ignores Compose 1.13's unified skiko-awt-runtime-all JAR
// (https://github.com/NucleusFramework/Nucleus/pull/934).
tasks.withType<AbstractJPackageTask>().configureEach {
    val outputDir = destinationDir
    doLast {
        val os = System.getProperty("os.name").lowercase(Locale.ROOT)
        val arch =
            when (System.getProperty("os.arch").lowercase(Locale.ROOT)) {
                "aarch64",
                "arm64" -> "arm64"
                else -> "x64"
            }
        val primaryLib =
            when {
                os.contains("linux") -> "libskiko-linux-$arch.so"
                os.contains("mac") -> "libskiko-macos-$arch.dylib"
                os.startsWith("win") -> "skiko-windows-$arch.dll"
                else -> return@doLast
            }
        val entriesToUnpack =
            if (os.startsWith("win")) setOf(primaryLib, "icudtl.dat") else setOf(primaryLib)
        val skikoNativeRegex =
            Regex("^(lib)?skiko-(linux|macos|windows)-[a-z0-9]+\\.(so|dylib|dll)$")

        val runtimeJar =
            outputDir.get().asFile.walkTopDown().firstOrNull {
                it.isFile && it.name.startsWith("skiko-awt-runtime-all-") && it.extension == "jar"
            } ?: return@doLast

        val appLibDir = runtimeJar.parentFile
        if (entriesToUnpack.all { appLibDir.resolve(it).exists() }) return@doLast

        val tempJar = runtimeJar.resolveSibling("${runtimeJar.name}.tmp")
        val stagedFiles = mutableListOf<Pair<File, File>>()
        try {
            ZipInputStream(runtimeJar.inputStream().buffered()).use { zis ->
                ZipOutputStream(tempJar.outputStream().buffered()).use { zos ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        val fileName = entry.name.substringAfterLast("/")
                        val baseName = fileName.removeSuffix(".sha256")
                        if (!entry.isDirectory && baseName in entriesToUnpack) {
                            val target = appLibDir.resolve(fileName)
                            val staged = appLibDir.resolve("$fileName.tmp")
                            staged.outputStream().buffered().use { out -> zis.copyTo(out) }
                            stagedFiles.add(staged to target)
                        } else if (
                            entry.isDirectory ||
                                (!skikoNativeRegex.matches(baseName) && baseName != "icudtl.dat")
                        ) {
                            zos.putNextEntry(ZipEntry(entry.name).apply { time = entry.time })
                            if (!entry.isDirectory) {
                                zis.copyTo(zos)
                            }
                            zos.closeEntry()
                        }
                        entry = zis.nextEntry
                    }
                }
            }

            val unpackedNames = stagedFiles.map { (_, target) -> target.name }.toSet()
            check(entriesToUnpack.all { it in unpackedNames }) {
                "Expected Skiko native entries $entriesToUnpack not found in ${runtimeJar.name}"
            }

            for ((staged, target) in stagedFiles) {
                Files.move(staged.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            Files.move(tempJar.toPath(), runtimeJar.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } finally {
            tempJar.delete()
            stagedFiles.forEach { (staged, _) -> staged.delete() }
        }
    }
}

fun execute(vararg command: String): String =
    providers.exec { commandLine(*command) }.standardOutput.asText.get().trim()
