package de.nilsdruyen.snappy.extensions

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCancellableCoroutine { continuation ->
  ProcessCameraProvider.getInstance(this).also { cameraProvider ->
    cameraProvider.addListener({
      continuation.resume(cameraProvider.get())
    }, ContextCompat.getMainExecutor(this))
  }
}