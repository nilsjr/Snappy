package de.nilsdruyen.snappy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.nilsdruyen.snappy.Constants.EXTRA_CONFIG
import de.nilsdruyen.snappy.Constants.EXTRA_IMAGES
import de.nilsdruyen.snappy.Constants.EXTRA_RESULT_EXCEPTION
import de.nilsdruyen.snappy.Constants.RESULT_ERROR
import de.nilsdruyen.snappy.Constants.RESULT_MISSING_PERMISSION
import de.nilsdruyen.snappy.components.CameraScreen
import de.nilsdruyen.snappy.components.ui.SnappyTheme
import de.nilsdruyen.snappy.controllers.FileControllerImpl
import de.nilsdruyen.snappy.extensions.toModel
import de.nilsdruyen.snappy.extensions.viewModelBuilder
import de.nilsdruyen.snappy.models.ParcelableSnappyConfig
import de.nilsdruyen.snappy.models.SnappyConfig
import java.io.File

internal class SnappyActivity : ComponentActivity() {

  private val viewModel: SnappyViewModel by viewModelBuilder {
    SnappyViewModel(FileControllerImpl(contentResolver))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val config = intent?.getParcelableExtra<ParcelableSnappyConfig>(EXTRA_CONFIG)?.toModel()
      ?: SnappyConfig(File(Environment.DIRECTORY_DCIM))

    viewModel.setConfig(config)

    setContent {
      SnappyTheme {
        CameraScreen(
          viewModel = viewModel,
          permissionDenied = {
            setResult(RESULT_MISSING_PERMISSION, null)
            finish()
          },
          saveImages = {
            onSuccess(ArrayList(it))
          },
          onError = {
            onFailure(it)
          },
        )
      }
    }
  }

  private fun onSuccess(result: ArrayList<Uri>) {
    setResult(
      Activity.RESULT_OK,
      Intent().apply {
        putParcelableArrayListExtra(EXTRA_IMAGES, result)
      }
    )
    finish()
  }

  private fun onFailure(exception: Exception) {
    setResult(RESULT_ERROR, Intent().putExtra(EXTRA_RESULT_EXCEPTION, exception))
    finish()
  }
}