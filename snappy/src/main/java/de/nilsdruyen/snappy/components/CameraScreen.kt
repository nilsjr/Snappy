package de.nilsdruyen.snappy.components

import android.net.Uri
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.nilsdruyen.snappy.SnappyViewModel

@Composable
internal fun CameraScreen(
  viewModel: SnappyViewModel,
  permissionDenied: () -> Unit,
  saveImages: (List<Uri>) -> Unit,
  onError: (ImageCaptureException) -> Unit,
) {
  val state by viewModel.state.collectAsState()

  Crossfade(targetState = state.screen) {
    when (it) {
      is SnappyScreen.Permissions -> {
        CameraPermissions(
          permissionGranted = viewModel::showCamera,
          permissionDenied = permissionDenied,
        )
      }
      is SnappyScreen.Camera -> {
        Camera(
          viewModel = viewModel,
          saveImages = saveImages,
          onError = onError,
        )
      }
      is SnappyScreen.Gallery-> {
        Gallery(viewModel, it.page)
      }
    }
  }
}