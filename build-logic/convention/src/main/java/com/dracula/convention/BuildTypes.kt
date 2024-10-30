package com.dracula.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Configures the build types for the Android project.
 *
 * @param commonExtension The common extension for Android build configurations.
 * @param extensionType The type of extension (APPLICATION or LIBRARY).
 */
internal fun Project.configureBuildTypes(
	commonExtension: CommonExtension<*, *, *, *, *, *>,
	extensionType: ExtensionType,
) {
	commonExtension.run {
		// Retrieve the API key from the local properties file
		val apiKey = gradleLocalProperties(rootDir, providers).getProperty("API_KEY") ?: ""
		buildFeatures {
			// Enable build config features
			buildConfig = true
		}
		// Configure build types based on the extension type
		when (extensionType) {
			ExtensionType.APPLICATION -> {
				extensions.configure<ApplicationExtension> {
					buildTypes {
						debug {
							// Configure the debug build type
							configureDebugBuildType(
								apiKey = apiKey,
							)
						}

						release {
							// Configure the release build type
							configureReleaseBuildType(
								apiKey = apiKey,
								commonExtension = commonExtension,
							)
						}
					}
				}
			}

			ExtensionType.LIBRARY -> {
				extensions.configure<LibraryExtension> {
					buildTypes {
						debug {
							// Configure the debug build type
							configureDebugBuildType(
								apiKey = apiKey,
							)
						}

						release {
							// Configure the release build type
							configureReleaseBuildType(
								apiKey = apiKey,
								commonExtension = commonExtension,
							)
						}
					}
				}
			}

			ExtensionType.DYNAMIC_FEATURE -> {
				extensions.configure<DynamicFeatureExtension> {
					buildTypes {
						debug {
							// Configure the debug build type
							configureDebugBuildType(
								apiKey = apiKey,
							)
						}

						release {
							// Configure the release build type
							configureReleaseBuildType(
								apiKey = apiKey,
								commonExtension = commonExtension,
								isMinifyEnabled = false,
							)
						}
					}
				}
			}
		}
	}
}

private fun BuildType.configureDebugBuildType(
	apiKey: String,
) {
	buildConfigField("String", "API_KEY", "\"$apiKey\"")
	buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")
}

private fun BuildType.configureReleaseBuildType(
	commonExtension: CommonExtension<*, *, *, *, *, *>,
	apiKey: String,
	isMinifyEnabled: Boolean = true,
) {
	buildConfigField("String", "API_KEY", "\"$apiKey\"")
	buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")
	this.isMinifyEnabled = isMinifyEnabled
	proguardFiles(
		commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
		"proguard-rules.pro"
	)
}