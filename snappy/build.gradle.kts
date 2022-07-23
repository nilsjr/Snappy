import kotlin.math.sign

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.kotlin.parcelize.get().pluginId)
  alias(libs.plugins.kotlin.dokka)
  `maven-publish`
  signing
}

android {
  resourcePrefix = "snappy"
  buildFeatures {
    buildConfig = false
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
  publishing {
    singleVariant("release")
  }
}

dependencies {
  implementation(libs.androidx.core)
  implementation(libs.androidx.windows)
  implementation(libs.androidx.lifecycle.runtime)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.google.materialDesign)

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

//tasks.register<Jar>("androidJavadocJar") {
//  archiveClassifier.set("javadoc")
//  from("$buildDir/dokka/javadoc")
//  dependsOn("dokkaJavadoc")
//}

tasks.register<Jar>("androidSourcesJar") {
  archiveClassifier.set("sources")
  from(android.sourceSets.getByName("main").java.srcDirs, android.sourceSets.getByName("release").java.srcDirs)
}

afterEvaluate {
  publishing {
    publications {
      create<MavenPublication>("release") {
        from(components["release"])
        artifactId = "snappy"
//        artifact(tasks.named("androidJavadocJar"))
        artifact(tasks.named("androidSourcesJar"))
        pom {
          name.set("snappy")
          description.set("Android camera library for taking quick & easy snapshots called Snappy")
          url.set("https://github.com/nilsjr/Snappy")
          licenses {
            license {
              name.set("MIT License")
              url.set("https://opensource.org/licenses/MIT")
            }
          }
          developers {
            developer {
              id.set("nilsjr")
              name.set("Nils Druyen")
              email.set("info@nilsdruyen.de")
            }
          }
          scm {
            connection.set("https://github.com/nilsjr/Snappy.git")
            developerConnection.set("https://github.com/nilsjr/Snappy.git")
            url.set("https://github.com/nilsjr/Snappy")
          }
        }
      }
    }
    repositories {
      maven {
        name = "sonatype"
        url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        credentials {
          username = findStringProperty("sonatypeUsername")
          password = findStringProperty("sonatypePassword")
        }
      }
    }
  }
}

signing {
  findStringProperty("signing.keyId")
  findStringProperty("signing.password")
  findStringProperty("signing.secretKeyRingFile")
  sign(publishing.publications)
}

fun Project.findStringProperty(propertyName: String): String? {
  return findProperty(propertyName) as String? ?: run {
    println("$propertyName missing in gradle.properties")
    null
  }
}