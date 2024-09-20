import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.jetbrainsKotlinAndroid) apply false
	alias(libs.plugins.kotlin.serialization) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.room) apply false
	alias(libs.plugins.android.library) apply false
	alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
	alias(libs.plugins.androidDynamicFeature) apply false
	alias(libs.plugins.mapsplatform.secrets.plugin) apply false
	alias(libs.plugins.compose.compiler) apply false
//	alias(libs.plugins.compose.compiler) apply false
}

allprojects {
	tasks.withType<KotlinCompile>().all {
		kotlinOptions {
			freeCompilerArgs += listOf(
				"-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
				"-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
				"-Xuse-experimental=kotlinx.coroutines.InternalCoroutinesApi",
				"-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi",
				"-Xuse-experimental=androidx.compose.ExperimentalComposeApi",
				"-Xuse-experimental=androidx.compose.material.ExperimentalMaterialApi",
				"-Xuse-experimental=androidx.compose.runtime.ExperimentalComposeApi",
				"-Xuse-experimental=androidx.compose.ui.ExperimentalComposeUiApi",
				"-Xuse-experimental=coil.annotation.ExperimentalCoilApi",
				"-Xuse-experimental=kotlinx.serialization.ExperimentalSerializationApi",
				"-Xuse-experimental=com.google.accompanist.pager.ExperimentalPagerApi",
				"-Xuse-experimental=androidx.compose.foundation.ExperimentalFoundationApi",
			)
		}
	}
}