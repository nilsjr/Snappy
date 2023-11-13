@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.android.application.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
}

android {
  namespace = "de.nilsdruyen.snappysample"
  defaultConfig {
    applicationId = "de.nilsdruyen.snappysample"
    versionCode = 1
    versionName = "1.0"
  }
  buildTypes {
    getByName("release") {
      isShrinkResources = true
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
}

dependencies {
  implementation(project(":snappy"))

  implementation(libs.google.material)

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.activity)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.material.icons)
  implementation(libs.androidx.compose.material3)

  implementation(libs.androidx.compose.uiToolingPreview)
  debugImplementation(libs.androidx.compose.uiTooling)

  implementation(libs.coil.compose)
}