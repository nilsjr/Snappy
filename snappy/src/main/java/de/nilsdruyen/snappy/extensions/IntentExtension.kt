package de.nilsdruyen.snappy.extensions

import android.content.Intent
import android.net.Uri
import de.nilsdruyen.snappy.Constants

internal fun Intent?.toSnappyData(): List<Uri> =
  this?.getParcelableArrayListExtra(Constants.EXTRA_IMAGES) ?: emptyList()

internal fun Intent?.getRootException(): Exception {
  this?.getSerializableExtra(Constants.EXTRA_RESULT_EXCEPTION).let {
    return if (it is Exception) it else IllegalStateException("Could retrieve root exception")
  }
}