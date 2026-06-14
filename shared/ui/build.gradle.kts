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

import org.gradle.api.plugins.ExtensionAware

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.koin.compiler)
}

val desktopOnly = providers.gradleProperty("desktopOnly").orNull?.toBoolean() ?: false

if (!desktopOnly) {
    apply(plugin = rootProject.libs.plugins.android.kotlin.multiplatform.library.get().pluginId)
}

kotlin {
    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        optIn.add(
            "androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi"
        )
    }

    if (!desktopOnly) {
        val androidExt = (this as ExtensionAware).extensions.getByName("android")
        androidExt.javaClass
            .getMethod("setNamespace", String::class.java)
            .invoke(androidExt, "com.shub39.rush.shared.ui")
        androidExt.javaClass
            .getMethod("setCompileSdk", Int::class.javaObjectType)
            .invoke(androidExt, libs.versions.compileSdk.get().toInt())
        androidExt.javaClass
            .getMethod("setMinSdk", Int::class.javaObjectType)
            .invoke(androidExt, libs.versions.minSdk.get().toInt())

        val androidResources =
            androidExt.javaClass.getMethod("androidResources", org.gradle.api.Action::class.java)
        androidResources.invoke(
            androidExt,
            org.gradle.api.Action<Any> {
                val rc: Any = this
                rc.javaClass
                    .getMethod("setEnable", Boolean::class.javaPrimitiveType)
                    .invoke(rc, true)
            },
        )
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

            implementation(libs.materialkolor)
            implementation(libs.zoomable)
            implementation(libs.landscapist.coil)
            implementation(libs.landscapist.placeholder)
            implementation(libs.colorpicker.compose)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.koin.annotations)
        }
        if (!desktopOnly) {
            findByName("androidMain")?.dependencies {
                implementation(project(":androidLibs:visualizer-helper"))
                implementation(libs.accompanist.permissions)
            }
        }
    }
}

dependencies {
    if (!desktopOnly) {
        add("androidRuntimeClasspath", libs.compose.ui.tooling)
        add("androidRuntimeClasspath", libs.compose.ui.tooling.preview)
    }
}

plugins.withId("com.android.kotlin.multiplatform.library") {
    val componentsExt = extensions.getByName("androidComponents")
    val selector = componentsExt.javaClass.getMethod("selector").invoke(componentsExt)
    val allSelector = selector.javaClass.getMethod("all").invoke(selector)

    val onVariantsMethod =
        componentsExt.javaClass.methods.first {
            it.name == "onVariants" &&
                it.parameterCount == 2 &&
                it.parameterTypes[1] == org.gradle.api.Action::class.java
        }

    onVariantsMethod.invoke(
        componentsExt,
        allSelector,
        org.gradle.api.Action<Any> {
            val variant = this
            val sources = variant.javaClass.getMethod("getSources").invoke(variant)
            val res = sources.javaClass.getMethod("getRes").invoke(sources)
            res?.javaClass
                ?.getMethod("addStaticSourceDirectory", String::class.java)
                ?.invoke(res, "src/commonMain/composeResources")
        },
    )
}
