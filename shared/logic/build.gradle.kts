import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.build.config)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.koin.compiler)
}

kotlin {
    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    android {
        namespace = "com.shub39.rush.shared.logic"
        compileSdk { version = release(libs.versions.compileSdk.get().toInt()) }
        minSdk { version = release(libs.versions.minSdk.get().toInt()) }
        withHostTest {}

        androidResources { enable = true }
    }

    jvm()

    sourceSets {
        commonTest.dependencies { implementation(kotlin("test")) }
        commonMain.dependencies {
            implementation(projects.shared.core)

            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.room.runtime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.koin.annotations)

            implementation(libs.landscapist.coil)
            implementation(libs.kmpalette.core)
            implementation(libs.bundles.ktor)
            implementation(libs.ksoup)
        }
        androidMain.dependencies {
            implementation(projects.androidLibs.romanization)
        }
        jvmMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }
    }
}

val localProperties = Properties()
val localFile = rootProject.file("local.properties")
if (localFile.exists()) localProperties.load(localFile.inputStream())

val publicGeniusApiToken = "\"qLSDtgIqHgzGNjOFUmdOxJKGJOg5RIAPzOKTfrs7rNxqYXwfdSh9HTHMJUs2X27Y\""

val privateToken = localProperties.getProperty("GENIUS_API_PRIVATE") ?: ""
if (privateToken.isBlank()) {
    println("WARNING: GENIUS_API_PRIVATE not found in local.properties")
}

buildConfig {
    className("Constants")
    packageName("com.shub39.rush.logic")
    useKotlinOutput { topLevelConstants = true }

    buildConfigField("GENIUS_API_TOKEN", privateToken.ifBlank { publicGeniusApiToken })
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspJvm", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
}

val generateChangelogJson by tasks.registering {
    description = "Extract changelogs from CHANGELOG.md"
    val inputFile = rootProject.file("CHANGELOG.md")
    val outputDir = file("$projectDir/src/commonMain/composeResources/files/")
    val outputFile = File(outputDir, "changelog.json")

    inputs.file(inputFile)
    outputs.file(outputFile)

    doLast {
        if (!outputDir.exists()) outputDir.mkdirs()

        val lines = inputFile.readLines()

        val map = mutableMapOf<String, MutableList<String>>()
        var currentVersion: String? = null

        for (line in lines) {
            when {
                line.startsWith("## ") -> {
                    currentVersion = line.removePrefix("## ").trim()
                    map[currentVersion] = mutableListOf()
                }

                line.startsWith("- ") && currentVersion != null -> {
                    map[currentVersion]?.add(line.removePrefix("- ").trim())
                }
            }
        }

        val json = buildString {
            append("[\n")

            map.entries.take(10).forEachIndexed { index, entry ->
                append("  {\n")
                append("    \"version\": \"${entry.key}\",\n")
                append("    \"changes\": [\n")

                entry.value.forEachIndexed { i, item ->
                    append("      \"${item.replace("\"", "\\\"")}\"")
                    if (i != entry.value.lastIndex) append(",")
                    append("\n")
                }

                append("    ]\n")
                append("  }")

                if (index != 9) append(",")
                append("\n")
            }

            append("]")
        }

        outputFile.writeText(json)
    }
}

tasks.named("copyNonXmlValueResourcesForCommonMain") { dependsOn(generateChangelogJson) }
