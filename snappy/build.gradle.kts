@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.kotlin.parcelize.get().pluginId)
//  `maven-publish`
//  signing
}

android {
  resourcePrefix = "snappy"
  buildFeatures {
    viewBinding = true
    buildConfig = false
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
}

dependencies {
  implementation(libs.androidx.core)
  implementation(libs.androidx.windows)
  implementation(libs.androidx.lifecycle.runtime)
  implementation(libs.androidx.lifecycle.viewmodel)

  implementation(libs.androidx.camera)
  implementation(libs.androidx.cameraLifecycle)
  implementation(libs.androidx.cameraPreview)

  implementation(libs.androidx.compose.activity)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.material.icons)
  implementation(libs.androidx.compose.material3)

  implementation(libs.androidx.compose.uiToolingPreview)
  debugImplementation(libs.androidx.compose.uiTooling)

  implementation(libs.coil.compose)

  implementation(libs.accompanist.pager)
  implementation(libs.accompanist.pager.indicators)
}

group = "de.nilsdruyen.snappy"
version = libs.versions.snappy.get()