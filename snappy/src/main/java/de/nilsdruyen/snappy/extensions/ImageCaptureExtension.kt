package de.nilsdruyen.snappy.extensions

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.net.toFile
import androidx.core.net.toUri
import de.nilsdruyen.snappy.FILENAME
import de.nilsdruyen.snappy.PHOTO_EXTENSION
import de.nilsdruyen.snappy.PREFIX
import de.nilsdruyen.snappy.TAG
import de.nilsdruyen.snappy.utils.PathUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("LongParameterList")
internal fun ImageCapture.takePicture(
  context: Context,
  outputDirectory: File,
  lensFacing: Int,
  onImageCaptured: (Uri) -> Unit,
  onError: (ImageCaptureException) -> Unit,
  cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
) {
  val photoFile = createFile(outputDirectory, PREFIX, FILENAME, PHOTO_EXTENSION)
  val outputFileOptions = getOutputFileOptions(context.contentResolver, lensFacing, photoFile)

  Log.d(TAG, "Path target: ${photoFile.absolutePath}")

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

        Log.d(TAG, "Photo capture succeeded: $savedUri - ${file.absolutePath}")

        val mimeType = MimeTypeMap.getSingleton()
          .getMimeTypeFromExtension(file.extension)
        MediaScannerConnection.scanFile(
          context,
          arrayOf(file.absolutePath),
          arrayOf(mimeType)
        ) { _, _ -> }
        onImageCaptured(file.toUri())
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

  val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    val path = photoFile.relativeTo(Environment.getExternalStorageDirectory())
    val fileName = photoFile.name
    val relativePath = path.absolutePath
      .dropLast(fileName.length)
      .removePrefix("/")
      .removeSuffix("/")

    Log.d(TAG, "Path build: ${path.absolutePath} - $relativePath")

    val contentValues = ContentValues().apply {
      put(MediaStore.MediaColumns.DISPLAY_NAME, photoFile.nameWithoutExtension)
      put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
      put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
    }
    ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
  } else {
    ImageCapture.OutputFileOptions.Builder(photoFile)
  }

  return builder.setMetadata(metadata).build()
}

private fun createFile(baseFolder: File, prefix: String, format: String, extension: String): File {
  if (!baseFolder.exists()) baseFolder.mkdir()
  return File(
    baseFolder,
    "$prefix-${SimpleDateFormat(format, Locale.getDefault()).format(System.currentTimeMillis())}$extension"
  )
}