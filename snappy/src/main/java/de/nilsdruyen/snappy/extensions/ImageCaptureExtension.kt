package de.nilsdruyen.snappy.extensions

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.net.toFile
import de.nilsdruyen.snappy.Constants.TAG
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val PHOTO_EXTENSION = ".jpg"

internal fun ImageCapture.takePicture(
  context: Context,
  outputDirectory: File,
  lensFacing: Int,
  onImageCaptured: (Uri) -> Unit,
  onError: (ImageCaptureException) -> Unit,
  cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
) {
  // Create output file to hold the image
  val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
  val outputFileOptions = getOutputFileOptions(lensFacing, photoFile)

  this.takePicture(
    outputFileOptions,
    cameraExecutor,
    object : ImageCapture.OnImageSavedCallback {
      override fun onImageSaved(output: ImageCapture.OutputFileResults) {
        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
        Log.d(TAG, "Photo capture succeeded: $savedUri")
        val mimeType = MimeTypeMap.getSingleton()
          .getMimeTypeFromExtension(savedUri.toFile().extension)
        MediaScannerConnection.scanFile(
          context,
          arrayOf(savedUri.toFile().absolutePath),
          arrayOf(mimeType)
        ) { _, _ -> }
        onImageCaptured(savedUri)
      }

      override fun onError(exception: ImageCaptureException) {
        onError(exception)
      }
    })
}

private fun getOutputFileOptions(
  lensFacing: Int,
  photoFile: File
): ImageCapture.OutputFileOptions {

  // Setup image capture metadata
  val metadata = ImageCapture.Metadata().apply {
    // Mirror image when using the front camera
    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
  }
  // Create output options object which contains file + metadata

//  val contentValues = ContentValues().apply {
//    put(MediaStore.MediaColumns.DISPLAY_NAME, "anImage")
//    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//      put(MediaStore.MediaColumns.RELATIVE_PATH, "Snappy")
//    }
//  }
//        val outputOptions = ImageCapture.OutputFileOptions
//          .Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//          .build()
  return ImageCapture.OutputFileOptions.Builder(photoFile)
    .setMetadata(metadata)
    .build()
}

private fun createFile(baseFolder: File, format: String, extension: String): File {
//        if (!config.outputDirectory.exists()) config.outputDirectory.mkdir()
        // TODO: set correct filename & path
//        val photoFile = FileUtils.createFile(config.outputDirectory, FILENAME, PHOTO_EXTENSION)
//        val imageFile = File(config.outputDirectory, "anImage.jpeg")
  return File(
    baseFolder, SimpleDateFormat(format, Locale.getDefault()).format(System.currentTimeMillis()) + extension
  )
}