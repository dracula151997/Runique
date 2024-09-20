import com.android.build.api.dsl.ApplicationExtension
import com.dracula.convention.ExtensionType
import com.dracula.convention.configureBuildTypes
import com.dracula.convention.configureKotlinAndroid
import com.dracula.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.run {
			pluginManager.run {
				apply("com.android.application")
				apply("org.jetbrains.kotlin.android")
			}

			extensions.configure<ApplicationExtension> {
				defaultConfig {
					applicationId = libs.findVersion("projectApplicationId").get().toString()
					minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()
					targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
					versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
					versionName = libs.findVersion("projectVersionName").get().toString()
				}

				configureKotlinAndroid(commonExtensions = this)
				configureBuildTypes(
					commonExtension = this,
					extensionType = ExtensionType.APPLICATION
				)
			}
		}
	}

}