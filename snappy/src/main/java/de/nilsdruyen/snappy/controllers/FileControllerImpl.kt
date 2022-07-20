package de.nilsdruyen.snappy.controllers

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toFile
import de.nilsdruyen.snappy.models.SnappyImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class FileControllerImpl(
  private val contentResolver: ContentResolver
) : FileController {

  override suspend fun deleteImage(image: SnappyImage) {
    try {
      if (image.uri.path?.contains("file://") == true) {
        image.uri.toFile().delete()
      } else {
        contentResolver.delete(image.uri, null, null)
      }
    } catch (securityException: SecurityException) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        val recoverableSecurityException =
//          securityException as? RecoverableSecurityException
//            ?: throw securityException
        // Signal to the Activity that it needs to request permission and
        // try the delete again if it succeeds.
      } else {
        throw securityException
      }
    }
  }

  override suspend fun getImages(): List<SnappyImage> {
    return withContext(Dispatchers.IO) {
      val images = mutableListOf<SnappyImage>()

      val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED
      )

      contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        MediaStore.Audio.Media.DATA + " like ? ",
        arrayOf("%Snappy%"),
        null
      )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
          val id = cursor.getLong(idColumn)
          val contentUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
          )
          images.add(SnappyImage(contentUri))
        }
      }

      images
    }
  }
}