plugins {
	alias(libs.plugins.runique.android.library)
	alias(libs.plugins.runique.android.room)
}

android {
	namespace = "com.dracula.core.database"
	compileOptions {
		isCoreLibraryDesugaringEnabled = true
	}
}

dependencies {
	implementation(libs.org.mongodb.bson)
	implementation(projects.core.domain)
	coreLibraryDesugaring(libs.desugar.jdk.libs)
	implementation(libs.bundles.koin)
}