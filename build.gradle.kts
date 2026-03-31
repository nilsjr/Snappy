import com.android.build.gradle.BaseExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
//  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.kotlin.dokka) apply false
  alias(libs.plugins.kotlin.parcelize) apply false
  alias(libs.plugins.misc.detekt) apply false
  alias(libs.plugins.misc.gradleVersions)
}

subprojects {
  apply(plugin = rootProject.libs.plugins.misc.detekt.get().pluginId)
  extensions.configure<DetektExtension> {
    toolVersion = rootProject.libs.versions.detekt.get()
    config.setFrom(files("$rootDir/detekt.yml"))
    buildUponDefaultConfig = true
    ignoredBuildTypes = listOf("release")
  }
  dependencies {
    add("detektPlugins", rootProject.libs.misc.detektFormatting)
  }
  tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
  }
  tasks.withType<Test> {
    useJUnitPlatform()
    failFast = true
  }
  tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
      allWarningsAsErrors.set(true)
      progressiveMode.set(true)
      freeCompilerArgs.addAll(
        listOfNotNull(
          "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
          "-Xexplicit-api=strict".takeIf { (this@subprojects.name != "sample") },
        )
      )
    }
  }
  afterEvaluate {
    extensions.configure<BaseExtension> {
      compileSdkVersion(libs.versions.androidconfig.compileSdk.get().toInt())
      defaultConfig {
        minSdk = libs.versions.androidconfig.minSdk.get().toInt()
        targetSdk = libs.versions.androidconfig.targetSdk.get().toInt()
      }
      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
      }
    }
  }

  tasks.register<Detekt>("ktlintFormat") {
    description = "Run detekt ktlint wrapper"
    parallel = true
    setSource(files("src/main/kotlin"))
    config.setFrom(files("$rootDir/detekt.yml"))
    buildUponDefaultConfig = true
    disableDefaultRuleSets = true
    autoCorrect = true
    reports {
      xml {
        required.set(true)
        outputLocation.set(layout.buildDirectory.file("reports/detekt/detektFormatting.xml"))
      }
      html.required.set(false)
      txt.required.set(false)
    }
    include(listOf("**/*.kt", "**/*.kts"))
    exclude("build/")
    dependencies {
      "detektPlugins"(libs.misc.detektFormatting)
    }
  }
}

tasks.dependencyUpdates.configure {
  gradleReleaseChannel = "current"

  fun releaseType(version: String): Int {
    val qualifiers = listOf("alpha", "beta", "m", "rc")
    val index = qualifiers.indexOfFirst { version.matches(".*[.\\-]$it[.\\-\\d]*".toRegex(RegexOption.IGNORE_CASE)) }
    return if (index < 0) qualifiers.size else index
  }

  rejectVersionIf { releaseType(candidate.version) < releaseType(currentVersion) }
}

tasks.register<Delete>("clean") {
  delete(layout.buildDirectory)
}