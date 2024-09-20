package com.dracula.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
	commonExtension: CommonExtension<*, *, *, *, *, *>,
	extensionType: ExtensionType,
) {
	commonExtension.run {
		val apiKey = gradleLocalProperties(rootDir, providers).getProperty("API_KEY") ?: ""
		buildFeatures {
			buildConfig = true
		}
		when (extensionType) {
			ExtensionType.APPLICATION -> {
				extensions.configure<ApplicationExtension> {
					buildTypes {
						debug {
							configureDebugBuildType(
								apiKey = apiKey,
							)
						}

						release {
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
							configureDebugBuildType(
								apiKey = apiKey,
							)
						}

						release {
							configureReleaseBuildType(
								apiKey = apiKey,
								commonExtension = commonExtension,
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
) {
	buildConfigField("String", "API_KEY", "\"$apiKey\"")
	buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")
	isMinifyEnabled = true
	proguardFiles(
		commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
		"proguard-rules.pro"
	)
}