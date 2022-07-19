package de.nilsdruyen.snappy.controllers

import android.content.ContentResolver
import android.os.Build
import android.util.Log
import androidx.core.net.toFile
import de.nilsdruyen.snappy.models.SnappyImage

internal class FileControllerImpl(
  private val contentResolver: ContentResolver
) : FileController {

  override suspend fun deleteImage(image: SnappyImage) {
    try {
      if (image.uri.toFile().delete()) {
        Log.d("FileController", "${image.uri} deleted")
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
//          pendingDeleteImage = image
//          _permissionNeededForDelete.postValue(
//            recoverableSecurityException.userAction.actionIntent.intentSender
//          )
      } else {
        throw securityException
      }
    }
  }
}