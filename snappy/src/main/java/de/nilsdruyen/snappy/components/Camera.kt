package de.nilsdruyen.snappy.components

import android.net.Uri
import android.view.Surface
import androidx.camera.core.AspectRatio
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import de.nilsdruyen.snappy.LocalSnappyConfig
import de.nilsdruyen.snappy.R
import de.nilsdruyen.snappy.SnappyViewModel
import de.nilsdruyen.snappy.extensions.getCameraProvider
import de.nilsdruyen.snappy.extensions.takePicture
import de.nilsdruyen.snappy.models.SnappyImage
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Suppress("LongMethod")
@Composable
internal fun Camera(
  viewModel: SnappyViewModel,
  saveImages: (List<String>) -> Unit,
  onError: (ImageCaptureException) -> Unit
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val config = LocalSnappyConfig.current
  val configuration = LocalConfiguration.current

  val screenAspectRatio = aspectRatio(configuration.screenWidthDp, configuration.screenHeightDp)

  val rotation: Int = when (configuration.orientation) {
    in 45..134 -> Surface.ROTATION_270
    in 135..224 -> Surface.ROTATION_180
    in 225..314 -> Surface.ROTATION_90
    else -> Surface.ROTATION_0
  }
  val lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
  val imageCapture: ImageCapture = remember {
    ImageCapture.Builder()
      .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
      .setTargetAspectRatio(screenAspectRatio)
      .setTargetRotation(rotation)
      .build()
  }
  val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

  val preview = Preview.Builder()
    .setTargetAspectRatio(screenAspectRatio)
    .setTargetRotation(rotation)
    .build()
  val previewView = remember { PreviewView(context) }

  val state by viewModel.state.collectAsState()

  val onImageCaptured: (Uri) -> Unit = { uri ->
    if (config.once) {
      saveImages(listOf(uri.toString()))
    } else {
      viewModel.addImage(SnappyImage(uri.toString()))
    }
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
      CameraControls {
        when (it) {
          is CameraEvent.TakePicture -> {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            imageCapture.takePicture(context, config.outputDirectory, lensFacing, onImageCaptured, onError)
          }
        }
      }
      if (!config.once) {
        ImageList(viewModel)
      }
    }
    if (!config.once) {
      SaveButton {
        saveImages(state.images.map { it.path })
      }
    }
  }
}

private const val RATIO_4_3_VALUE = 4.0 / 3.0
private const val RATIO_16_9_VALUE = 16.0 / 9.0

private fun aspectRatio(width: Int, height: Int): Int {
  val previewRatio = max(width, height).toDouble() / min(width, height)
  if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
    return AspectRatio.RATIO_4_3
  }
  return AspectRatio.RATIO_16_9
}

@Composable
internal fun CameraControls(
  cameraUIAction: (CameraEvent) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
  ) {
    CameraControl(
      Icons.Sharp.Lens,
      R.string.snappy_take_image,
      modifier = Modifier
        .align(Alignment.Center)
        .size(72.dp)
        .padding(1.dp)
        .border(1.dp, Color.White, CircleShape),
      onClick = { cameraUIAction(CameraEvent.TakePicture) }
    )
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