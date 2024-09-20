plugins {
	alias(libs.plugins.runique.library.jvm)
}

dependencies {
	implementation(projects.core.domain)
}