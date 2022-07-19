package de.nilsdruyen.snappy.models

import android.net.Uri

public sealed interface SnappyResult {

  public data class Success(val images: List<Uri>) : SnappyResult
  public object Canceled : SnappyResult
  public object PermissionDenied : SnappyResult
  public data class Error(val exception: Exception) : SnappyResult
}