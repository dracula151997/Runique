package com.dracula.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configures the Android project to use Jetpack Compose.
 *
 * @param commonExtension The common extension for Android build configurations.
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.run {
        buildFeatures {
            // Enable Jetpack Compose
            compose = true
        }
        dependencies {
            // Retrieve the Compose BOM (Bill of Materials) library
            val composeBom = libs
                .findLibrary("androidx.compose.bom")
                .get()
            // Add the Compose BOM to the implementation dependencies
            "implementation"(platform(composeBom))
            // Add the Compose BOM to the Android test implementation dependencies
            "androidTestImplementation"(platform(composeBom))
            // Add the Compose UI tooling preview library to the debug implementation dependencies
            "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
        }
    }
}