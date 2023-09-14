package de.nilsdruyen.snappy

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import de.nilsdruyen.snappy.components.CameraScreen
import de.nilsdruyen.snappy.components.ui.SnappyTheme
import de.nilsdruyen.snappy.controllers.FileControllerImpl
import de.nilsdruyen.snappy.extensions.toModel
import de.nilsdruyen.snappy.extensions.viewModelBuilder
import de.nilsdruyen.snappy.models.ParcelableSnappyConfig
import de.nilsdruyen.snappy.models.SnappyConfig
import java.io.File

internal val LocalSnappyConfig: ProvidableCompositionLocal<SnappyConfig> =
  compositionLocalOf { error("No config provided") }

internal class SnappyActivity : ComponentActivity() {

  @Suppress("DEPRECATION")
  private val config by lazy {
    if (Build.VERSION.SDK_INT >= 33) {
      intent?.getParcelableExtra(EXTRA_CONFIG, ParcelableSnappyConfig::class.java)
    } else {
      intent?.getParcelableExtra(EXTRA_CONFIG)
    }?.toModel() ?: SnappyConfig(File(Environment.DIRECTORY_DCIM))
  }

  private val viewModel: SnappyViewModel by viewModelBuilder {
    SnappyViewModel(FileControllerImpl(config.outputDirectory, contentResolver))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      SnappyTheme {
        CompositionLocalProvider(LocalSnappyConfig provides config) {
          CameraScreen(
            viewModel = viewModel,
            permissionDenied = {
              setResult(RESULT_MISSING_PERMISSION, null)
              finish()
            },
            saveImages = { onSuccess(ArrayList(it)) },
            onError = this::onFailure,
          )
        }
      }
    }
  }

  private fun onSuccess(result: ArrayList<String>) {
    val name = "${config.packageName}.$EXTRA_IMAGES"
    setResult(
      Activity.RESULT_OK,
      Intent().apply {
        putStringArrayListExtra(name, result)
      }
    )
    finish()
  }

  private fun onFailure(exception: Exception) {
    setResult(RESULT_ERROR, Intent().putExtra(EXTRA_RESULT_EXCEPTION, exception))
    finish()
  }
}