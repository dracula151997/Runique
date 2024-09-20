plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrainsKotlinAndroid)
	alias(libs.plugins.runique.jvm.ktor)
}

android {
	namespace = "com.dracula.auth.data"
	compileSdk = 34
	defaultConfig {
		minSdk = 23
	}
}

dependencies {
	implementation(projects.core.domain)
	implementation(projects.core.data)
	implementation(projects.auth.domain)
	implementation(libs.bundles.koin)
}