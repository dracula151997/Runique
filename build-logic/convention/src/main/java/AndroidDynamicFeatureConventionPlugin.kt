import com.android.build.api.dsl.DynamicFeatureExtension
import com.dracula.convention.ExtensionType
import com.dracula.convention.configureAndroidCompose
import com.dracula.convention.configureBuildTypes
import com.dracula.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.run {
			pluginManager.run {
				apply("com.android.dynamic-feature")
				apply("org.jetbrains.kotlin.android")
			}

			extensions.configure<DynamicFeatureExtension> {
				configureKotlinAndroid(commonExtensions = this)
				configureAndroidCompose(commonExtension = this)
				configureBuildTypes(
					commonExtension = this,
					extensionType = ExtensionType.DYNAMIC_FEATURE
				)
			}
			dependencies {
				addUiLayerDependencies(target)
				"testImplementation"(kotlin("test"))
			}
		}
	}

}