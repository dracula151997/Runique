import com.android.build.gradle.LibraryExtension
import com.dracula.convention.ExtensionType
import com.dracula.convention.configureBuildTypes
import com.dracula.convention.configureKotlinAndroid
import com.dracula.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.run {
			pluginManager.run {
				apply("com.android.library")
				apply("org.jetbrains.kotlin.android")
			}

			extensions.configure<LibraryExtension> {
				configureKotlinAndroid(commonExtensions = this)
				configureBuildTypes(
					commonExtension = this,
					extensionType = ExtensionType.LIBRARY
				)
				defaultConfig {
					testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
					consumerProguardFiles("consumer-rules.pro")
					minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()
				}
			}

			dependencies {
				"testImplementation"(kotlin("test"))
			}
		}
	}
}