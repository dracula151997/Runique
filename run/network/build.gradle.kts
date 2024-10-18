plugins {
	alias(libs.plugins.runique.android.library)
	alias(libs.plugins.kotlin.serialization)
}

android {
	namespace = "com.dracula.run.network"
	compileOptions {
		isCoreLibraryDesugaringEnabled = true
	}
}

dependencies {
	implementation(projects.core.domain)
	implementation(projects.core.data)
	implementation(libs.bundles.ktor)
	implementation(libs.kotlinx.serialization.json)
	coreLibraryDesugaring(libs.desugar.jdk.libs)
}