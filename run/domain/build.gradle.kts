plugins {
	alias(libs.plugins.runique.library.jvm)
}

dependencies {
	implementation(libs.kotlinx.coroutines.core)
	implementation(projects.core.domain)
}