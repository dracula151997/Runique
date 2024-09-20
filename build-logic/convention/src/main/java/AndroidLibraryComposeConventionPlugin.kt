import com.android.build.gradle.LibraryExtension
import com.dracula.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.run {
			pluginManager.apply("runique.android.library")
			pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
			val extension = extensions.getByType(LibraryExtension::class.java)
			configureAndroidCompose(commonExtension = extension)
		}
	}
}