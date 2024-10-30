plugins {
	alias(libs.plugins.runique.android.feature.ui)
}

android {
	namespace = "com.dracula.analytics.presentation"
}

dependencies {
	implementation(projects.analytics.domain)
}