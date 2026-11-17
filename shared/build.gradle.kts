import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            linkerOpts("-lsqlite3")
        }
    }
    
    android {
       namespace = "com.aizid.playmov.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }

    val appVersionName = project.findProperty("app.versionName") as? String ?: "1.0.0"
    val appVersionCode = project.findProperty("app.versionCode") as? String ?: "1"

    val generateAppVersion by tasks.registering {
        description = "Generate AppVersion.kt"
        val outputDir = layout.buildDirectory.dir("generated/appVersion/kotlin")
        inputs.property("versionName", appVersionName)
        inputs.property("versionCode", appVersionCode)
        outputs.dir(outputDir)
        doLast {
            val versionFile = outputDir.get().file("com/aizid/playmov/AppVersion.kt").asFile
            versionFile.parentFile.mkdirs()
            versionFile.writeText("""
                package com.aizid.playmov
                
                object AppVersion {
                    const val VERSION_NAME = "$appVersionName"
                    const val VERSION_CODE = $appVersionCode
                }
            """.trimIndent())
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateAppVersion)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
        }

        commonMain.dependencies {

            // --------------------------------------------------
            // Compose
            // --------------------------------------------------
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)

            // Icons
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            // Navigation
            implementation(libs.navigation3.ui)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // --------------------------------------------------
            // Kotlin
            // --------------------------------------------------
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            // --------------------------------------------------
            // Ktor (Networking)
            // --------------------------------------------------
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // --------------------------------------------------
            // Sql Delight
            // --------------------------------------------------

            implementation(libs.sqldelight.coroutines.extensions)

            // --------------------------------------------------
            // Koin (DI)
            // --------------------------------------------------
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // --------------------------------------------------
            // WebView & Image Loading
            // --------------------------------------------------
            implementation(libs.compose.webview)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // --------------------------------------------------
            // Ksafe - Encrypted Key/Value pair data
            // --------------------------------------------------
            implementation(libs.ksafe)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            verifyMigrations = false
            packageName.set("com.aizid.playmov.data.db")
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}