package de.nilsdruyen.snappy.extensions

import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import de.nilsdruyen.snappy.EXTRA_IMAGES
import de.nilsdruyen.snappy.EXTRA_RESULT_EXCEPTION

internal fun Intent?.toSnappyData(): List<Uri> = this?.parcelableList(EXTRA_IMAGES, Uri::class.java) ?: emptyList()

@Suppress("DEPRECATION")
internal fun Intent?.getRootException(): Exception {
  if (SDK_INT >= 33) {
    this?.getSerializableExtra(EXTRA_RESULT_EXCEPTION, Exception::class.java)
  } else {
    this?.getSerializableExtra(EXTRA_RESULT_EXCEPTION)
  }.let {
    return if (it is Exception) it else IllegalStateException("Could retrieve root exception")
  }
}

private inline fun <reified T> Intent.parcelableList(key: String, clazz: Class<T>): List<T>? = when {
  SDK_INT >= 33 -> getParcelableArrayListExtra(key, clazz)
  else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}