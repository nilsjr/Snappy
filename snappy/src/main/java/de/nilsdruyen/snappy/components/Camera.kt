package de.nilsdruyen.snappy.components

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import de.nilsdruyen.snappy.R
import de.nilsdruyen.snappy.SnappyViewModel
import de.nilsdruyen.snappy.extensions.getCameraProvider
import de.nilsdruyen.snappy.extensions.takePicture
import de.nilsdruyen.snappy.models.SnappyImage

@Composable
internal fun Camera(
  viewModel: SnappyViewModel,
  saveImages: (List<Uri>) -> Unit,
  onError: (ImageCaptureException) -> Unit
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
  val imageCapture: ImageCapture = remember {
    ImageCapture.Builder()
      .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
      .build()
  }
  val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

  val preview = Preview.Builder().build()
  val previewView = remember { PreviewView(context) }

  val state by viewModel.state.collectAsState()

  val onImageCaptured: (Uri) -> Unit = { uri ->
    viewModel.addImage(SnappyImage(uri))
  }

  LaunchedEffect(lensFacing) {
    val cameraProvider = context.getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
    preview.setSurfaceProvider(previewView.surfaceProvider)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
      .navigationBarsPadding()
  ) {
    AndroidView({ previewView }, modifier = Modifier.fillMaxSize(), NoOpUpdate)

    Column(
      modifier = Modifier.align(Alignment.BottomCenter),
      verticalArrangement = Arrangement.Bottom
    ) {
      CameraControls(
        {
          when (it) {
            is CameraUiAction.OnCameraClick -> {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              imageCapture.takePicture(context, state.config.outputDirectory, lensFacing, onImageCaptured, onError)
            }
          }
        }
      )
      ImageList(viewModel)
    }

    IconButton(
      onClick = { saveImages(state.images.map { it.uri }) },
      modifier = Modifier
        .padding(16.dp)
        .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
        .align(Alignment.TopEnd)
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = null,
        tint = Color.White
      )
    }
  }
}


@Composable
internal fun CameraControls(cameraUIAction: (CameraUiAction) -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
//    horizontalArrangement = Arrangement.SpaceBetween,
//    verticalAlignment = Alignment.CenterVertically
  ) {
//    CameraControl(
//      Icons.Sharp.FlipCameraAndroid,
//      R.string.snappy_flip_camera,
//      modifier = Modifier.size(64.dp),
//      onClick = { cameraUIAction(CameraUiAction.OnSwitchCameraClick) }
//    )
    CameraControl(
      Icons.Sharp.Lens,
      R.string.snappy_take_image,
      modifier = Modifier
        .align(Alignment.Center)
        .size(64.dp)
        .padding(1.dp)
        .border(1.dp, Color.White, CircleShape),
      onClick = { cameraUIAction(CameraUiAction.OnCameraClick) }
    )
//    CameraControl(
//      Icons.Sharp.PhotoLibrary,
//      R.string.snappy_show_gallery,
//      modifier = Modifier
//        .size(64.dp)
//        .align(Alignment.CenterEnd),
//      onClick = { cameraUIAction(CameraUiAction.OnGalleryViewClick) }
//    )
  }
}

@Composable
internal fun CameraControl(
  imageVector: ImageVector,
  contentDescId: Int,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  IconButton(
    onClick = onClick,
    modifier = modifier
  ) {
    Icon(
      imageVector,
      contentDescription = stringResource(id = contentDescId),
      modifier = modifier,
      tint = Color.White
    )
  }
}