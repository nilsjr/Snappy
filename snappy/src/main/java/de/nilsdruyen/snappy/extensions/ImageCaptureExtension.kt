package de.nilsdruyen.snappy.extensions

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.net.toFile
import de.nilsdruyen.snappy.Constants.TAG
import de.nilsdruyen.snappy.utils.PathUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val PHOTO_EXTENSION = ".jpg"

@Suppress("LongParameterList")
internal fun ImageCapture.takePicture(
  context: Context,
  outputDirectory: File,
  lensFacing: Int,
  onImageCaptured: (Uri) -> Unit,
  onError: (ImageCaptureException) -> Unit,
  cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
) {
  val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
  val outputFileOptions = getOutputFileOptions(context.contentResolver, lensFacing, photoFile)

  this.takePicture(
    outputFileOptions,
    cameraExecutor,
    object : ImageCapture.OnImageSavedCallback {
      override fun onImageSaved(output: ImageCapture.OutputFileResults) {
        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)

        val file = if (savedUri.path?.contains("file://") == true) {
          savedUri.toFile()
        } else {
          File(PathUtils.getRealPath(context, savedUri) ?: "")
        }
        Log.d(TAG, "Photo capture succeeded: $savedUri")
        val mimeType = MimeTypeMap.getSingleton()
          .getMimeTypeFromExtension(file.extension)
        MediaScannerConnection.scanFile(
          context,
          arrayOf(file.absolutePath),
          arrayOf(mimeType)
        ) { _, _ -> }
        onImageCaptured(savedUri)
      }

      override fun onError(exception: ImageCaptureException) {
        onError(exception)
      }
    }
  )
}

private fun getOutputFileOptions(
  contentResolver: ContentResolver,
  lensFacing: Int,
  photoFile: File
): ImageCapture.OutputFileOptions {
  val metadata = ImageCapture.Metadata().apply {
    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
  }

  val contentValues = ContentValues().apply {
    put(MediaStore.MediaColumns.DISPLAY_NAME, photoFile.nameWithoutExtension)
    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      put(MediaStore.MediaColumns.RELATIVE_PATH, "Snappy")
    }
  }

  return ImageCapture.OutputFileOptions
    .Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    .setMetadata(metadata)
    .build()
//  return ImageCapture.OutputFileOptions.Builder(photoFile)
//    .setMetadata(metadata)
//    .build()
}

private fun createFile(baseFolder: File, format: String, extension: String): File {
  if (!baseFolder.exists()) baseFolder.mkdir()
  return File(
    baseFolder, SimpleDateFormat(format, Locale.getDefault()).format(System.currentTimeMillis()) + extension
  )
}