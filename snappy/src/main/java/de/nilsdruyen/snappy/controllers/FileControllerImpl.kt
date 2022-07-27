package de.nilsdruyen.snappy.controllers

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toFile
import de.nilsdruyen.snappy.PREFIX
import de.nilsdruyen.snappy.TAG
import de.nilsdruyen.snappy.models.SnappyImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal class FileControllerImpl(
  private val outputDirectory: File,
  private val contentResolver: ContentResolver
) : FileController {

  private val relativePath by lazy {
    outputDirectory.relativeTo(Environment.getExternalStorageDirectory()).absolutePath
      .removePrefix("/")
      .removeSuffix("/")
  }

  override suspend fun deleteImage(image: SnappyImage) {
    Log.i(TAG, "delete: ${image.uri}")
    try {
      image.uri.toFile().delete()
    } catch (exception: IllegalArgumentException) {
      Log.w(TAG, exception.message ?: "error delete image: IllegalArgumentException")
      contentResolver.delete(image.uri, null, null)
    }
  }

  override suspend fun getImages(): List<SnappyImage> {
    return withContext(Dispatchers.IO) {
      val images = mutableListOf<SnappyImage>()

      Log.d(TAG, "get images in path: $relativePath")

      val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED
      )

      contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        "${MediaStore.Audio.Media.DISPLAY_NAME} like ? ",
        arrayOf("%$PREFIX%"),
        null
      )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
          val id = cursor.getLong(idColumn)
          val contentUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id,
          )
          images.add(SnappyImage(contentUri))
        }
      }

      Log.d(TAG, "images: ${images.size}")

      images
    }
  }

  override suspend fun clearTempImages() {
    getImages().forEach {
      deleteImage(it)
    }
  }
}